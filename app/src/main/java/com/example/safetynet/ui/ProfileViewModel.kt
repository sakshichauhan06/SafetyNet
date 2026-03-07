package com.example.safetynet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.User
import com.example.safetynet.data.UserDao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<com.example.safetynet.data.User?> = flow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            emitAll(userDao.getUserById(uid))
        } else {
            emit(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateUserProfile(newName: String, newContact: String, newContactName: String) {
        val currentUid = firebaseAuth.currentUser?.uid ?: return
        val currentEmail = firebaseAuth.currentUser?.email ?: ""

        try {
            viewModelScope.launch {
                // Update ROOM
                val updatedUser = User(
                    uid = currentUid,
                    name = newName,
                    email = currentEmail,
                    emergencyContact = newContact,
                    emergencyContactName = newContactName
                )
                userDao.insertUser(updatedUser)

                // Update Firestore
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUid)
                    .set(updatedUser, SetOptions.merge()) // Merge instead of overwriting everything
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerSOS(currentUserLocation: String) {
        val user = currentUser.value
        val contact = user?.emergencyContact

        if (!contact.isNullOrBlank()) {

        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            userDao.clearUserData()
        }
    }
}