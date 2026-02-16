package com.example.safetynet.data

import androidx.compose.runtime.snapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    suspend fun savePin(safetyPin: SafetyPin): Result<Unit> {
        return try {
            // prepare the Firestore document
            val collection = firestore.collection("incidents")
            val documentRef = if(safetyPin.id.isEmpty()) collection.document()
            else collection.document(safetyPin.id)

            // create the final object with the correct ID
            val finalPin = safetyPin.copy(id = documentRef.id)

            // save to Firestore (Cloud)
            documentRef.set(finalPin).await()

            // save to Room (Local)
            safetyPinDao.insert(finalPin)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves all safety pins from the local database.
     *
     * @return Result.success with list of all pins, or empty list if none exist
     */
    // Update getAllPinsFromCloud to fetch from Cloud or Local
    suspend fun getAllPinsFromCloud(): Result<List<SafetyPin>> {
        return try {
            val snapshot = firestore.collection("incidents").get().await()
            val pins = snapshot.toObjects(SafetyPin::class.java)
            Result.success(pins)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

                if (snapshot != null) {
                    val pins = snapshot.toObjects(SafetyPin::class.java)
                    trySend(pins)
                }
            }
        awaitClose { subscription.remove() }
    }

}