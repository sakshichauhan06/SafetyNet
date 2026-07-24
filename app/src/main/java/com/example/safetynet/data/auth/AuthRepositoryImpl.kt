package com.example.safetynet.data.auth

import android.util.Log
import com.example.safetynet.data.UserDao
import com.example.safetynet.utils.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : AuthRepository {


    override fun loginUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())

        // Sign in via Firebase auth
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Login failed")

        // Fetch the existing profile from Firestore
        val snapshot = firestore.collection("users").document(uid).get().await()
        val userProfile = snapshot.toObject(com.example.safetynet.data.User::class.java)

        // sync to ROOM so the app has the profile offline
        userProfile?.let {
            userDao.insertUser(it)
        } ?: Log.w(TAG, "Login success but user profile missing in Firestore for UID: $uid")

        emit(Resource.Success(result))
    }.catch { e ->
        Log.e(TAG, "Login error: ${e.message}", e)
        emit(Resource.Error(e.message ?: "An unknown error occurred"))
    }

    override fun signupUser(
        email: String,
        password: String,
        name: String,
        phoneNumber: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())

        // Create user in Firebase auth
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User creation failed")
        println("DEBUG: Firebase auth success, uid=$uid")

        val normalizedPhone = phoneNumber.trim()
            .replace(Regex("\\s+"), "")
            .replace(Regex("[\\-()]"), "")

        val phoneWithCountryCode = if (normalizedPhone.startsWith("+")) {
            normalizedPhone
        } else if (normalizedPhone.length == 10) {
            "+91$normalizedPhone"
        } else {
            normalizedPhone
        }
        Log.d(TAG, "Normalized phone: $phoneWithCountryCode")

        // Create the user object
        val newUser = com.example.safetynet.data.User(
            uid = uid,
            email = email,
            name = name, // Placeholder name
            phoneNumber = phoneWithCountryCode,
            createdAt = System.currentTimeMillis()
        )
        Log.d(TAG, "User object structured: $newUser")

        // save to Firestore (remote)
        firestore.collection("users").document(uid).set(newUser).await()
        Log.d(TAG, "Firestore write completed")
        // VERIFY: Read it back immediately

        // save to ROOM (local)
        userDao.insertUser(newUser)
        Log.d(TAG, "Room insert completed")

        result.user?.sendEmailVerification()?.await()
        Log.i(TAG, "Email verification link dispatched successfully")

        emit(Resource.Success(result))
    }.catch { e ->
        Log.e(TAG, "Signup execution failed: ${e.message}", e)
        emit(Resource.Error(e.message ?: "An unknown error occurred"))
    }
}