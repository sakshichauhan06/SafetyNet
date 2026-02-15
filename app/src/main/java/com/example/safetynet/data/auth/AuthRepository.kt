package com.example.safetynet.data.auth

import com.example.safetynet.utils.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun loginUser(email: String, password: String) : Flow<Resource<AuthResult>>

    fun signupUser(email: String, password: String) : Flow<Resource<AuthResult>>

}