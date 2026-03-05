package com.example.safetynet.data

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Inserts a new safety pin into the local database.
 *
 * @param safetyPin The pin to insert
 * @return Result.success if inserted, Result.failure if database error occurs
 */
class SafetyPinRepository @Inject constructor(
    private val safetyPinDao: SafetyPinDao,
    private val firestore: FirebaseFirestore
) {

//    The UI should listen to this. It updates the moment
//    EITHER a local save happens OR a Firebase sync happens.
    fun getPinsFromRoom(): Flow<List<SafetyPin>> {
        return safetyPinDao.getAllPins()
    }
    suspend fun savePin(safetyPin: SafetyPin): Result<Unit> {
        return try {
            // prepare the Firestore document
            val collection = firestore.collection("incidents")
            val documentRef = if(safetyPin.id.isEmpty()) collection.document()
                            else collection.document(safetyPin.id)

            // create the final object with the correct ID
            val finalPin = safetyPin.copy(id = documentRef.id)

            // save to Room (Local)
            safetyPinDao.insert(finalPin)

            // save to Firestore (Cloud)
            documentRef.set(finalPin)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    Modify your listener to focus ONLY on syncing.
    // It doesn't need to 'trySend' to the UI anymore because Room handles that.
    fun startFirebaseSync() {
        firestore.collection("incidents")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val pins = querySnapshot.toObjects(SafetyPin::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        pins.forEach { pin ->
                            safetyPinDao.insert(pin)
                        }
                    }
                }
            }
    }
    suspend fun getAllPinsLocally(): List<SafetyPin> {
        return safetyPinDao.getAllPinsAsList()
    }

    /**
     * Deletes a specific safety pin by ID from the local database.
     */
    suspend fun deletePin(pinId: String): Result<Unit> {
        return try {
            firestore.collection("incidents").document(pinId).delete().await()

            safetyPinDao.deleteById(pinId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPinsRealtime(): Flow<List<SafetyPin>> = callbackFlow {
        val subscription = firestore.collection("incidents")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val pins = querySnapshot.toObjects(SafetyPin::class.java)

                    // 1. Send to the UI immediately
                    trySend(pins) // emits the new list of pins to the flow

                    // 2. Sync with the ROOM in the bg
                    CoroutineScope(Dispatchers.IO).launch {
                        pins.forEach { pin ->
                            safetyPinDao.insert(pin)
                        }
                    }
                }
            }

        // Stops the listener when the user leaves the screen
        awaitClose { subscription.remove() }
    }

}