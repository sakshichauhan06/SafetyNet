package com.example.safetynet.data.auth

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
        }

        emit(Resource.Success(result))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }

    override fun signupUser(
        email: String,
        password: String
    ): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())

        // Create user in Firebase auth
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User creation failed")

        // Create the user object
        val newUser = com.example.safetynet.data.User(
            uid = uid,
            email = email,
            name = email.substringBefore("@"), // Placeholder name
            createdAt = System.currentTimeMillis()
        )

        // save to Firestore (remote)
        firestore.collection("users").document(uid).set(newUser).await()

        // save to ROOM (local)
        userDao.insertUser(newUser)

        emit(Resource.Success(result))
    }.catch {
        emit(Resource.Error(it.message.toString()))
    }
}