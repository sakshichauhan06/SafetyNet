package com.example.safetynet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.UserDao
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            userDao.clearUserData()
        }
    }
}