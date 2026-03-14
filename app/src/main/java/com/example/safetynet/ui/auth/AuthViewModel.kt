package com.example.safetynet.ui.auth


import android.app.Activity
import android.provider.Telephony
import androidx.browser.trusted.Token
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.auth.AuthRepository
import com.example.safetynet.utils.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // To store the verification ID sent by Firebase
    private var verificationId: String? = null

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

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            try {
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Google Sign-In Failed")
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unexpected error: Google Sign-In Failed")
            }
        }
    }

    fun signInWithPhone(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Verification failed")
                    }
                }
        }
    }

    fun sendOtp(phoneNumber: String, activity: Activity, onCodeSent: (String) -> Unit) {
        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhone(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.message ?: "Verification failed")
            }

            override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                _authState.value = AuthState.Unauthenticated
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Guest Login failed")
                }
            }
        }
    }

    fun signup(email: String, password: String) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Fields cannot be empty")
            return
        }

        // Validate email format
        if (!isEmailValid(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid address.")
            return
        }

        // Validate Password length
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters long.")
            return
        }

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

    fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,6}\$"
        return email.matches(emailPattern.toRegex())
    }

    fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

