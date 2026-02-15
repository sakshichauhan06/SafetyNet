package com.example.safetynet.ui.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.auth.AuthRepository
import com.example.safetynet.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        if(firebaseAuth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.loginUser(email, password).collect { result ->
                _authState.value = when(result) {
                    is Resource.Loading -> AuthState.Loading
                    is Resource.Success -> AuthState.Authenticated
                    is Resource.Error -> AuthState.Error(result.message ?: "Login Failed")
                }
            }
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            repository.signupUser(email, password).collect { result ->
                _authState.value = when(result) {
                    is Resource.Loading -> AuthState.Loading
                    is Resource.Success -> AuthState.Authenticated
                    is Resource.Error -> AuthState.Error(result.message ?: "Signup Failed")
                }
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

