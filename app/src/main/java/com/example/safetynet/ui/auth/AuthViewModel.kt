package com.example.safetynet.ui.auth


import android.app.Activity
import android.provider.Telephony
import androidx.browser.trusted.Token
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.auth.AuthRepository
import com.example.safetynet.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
        val user = firebaseAuth.currentUser

        if(user == null) {
            _authState.value = AuthState.Unauthenticated
        } else if (!user.isEmailVerified && user.providerData.any {it.providerId == "password" }) {
            // if they logged in with Email/Password but haven't verified their email
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
        _authState.value = AuthState.Loading

        firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send Verification immediately
                    firebaseAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                _authState.value = AuthState.Authenticated
                            } else {
                                _authState.value = AuthState.Error("Account created, but faied to send verification email")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Signup Failed")
                }
            }
    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return email.matches(emailPattern.toRegex())
    }

    fun sendVerificationEmail() {
        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Error("Verification email sent. Please check your inbox.")
                }
            }
    }

    fun checkEmailVerificationStatus() {
        viewModelScope.launch {
            firebaseAuth.currentUser?.reload()?.addOnCompleteListener {
                val user = firebaseAuth.currentUser
                if (user?.isEmailVerified == true) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Email not verified yet.")
                }
            }
        }
    }

    fun resetPassword(email: String) {
        if (!isEmailValid(email)) {
            _authState.value = AuthState.Error("Enter a valid email to reset password")
            return
        }
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Error("Reset link sent to your email")
            } else {
                _authState.value = AuthState.Error(task.exception?.message ?: "Password reset failed")
            }
        }
    }

    fun signOut(context: android.content.Context) {
        // Sign out from Firebase
        firebaseAuth.signOut()

        // Sign out from Google (This clears the 'sticky' session)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        _authState.value = AuthState.Unauthenticated

        googleSignInClient.signOut()
    }
}

