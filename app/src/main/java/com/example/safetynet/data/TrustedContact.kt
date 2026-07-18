package com.example.safetynet.data

data class TrustedContact(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val relationship: String = "",
    val isAppUser: Boolean = false,
    val fcmToken: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
