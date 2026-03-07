package com.example.safetynet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null,
    val emergencyContact: String = "",
    val emergencyContactName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)