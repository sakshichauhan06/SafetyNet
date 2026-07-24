package com.example.safetynet.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.TrustedContact
import com.example.safetynet.data.TrustedContactRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class SOSViewModel @Inject constructor(
    private val trustedContactRepository: TrustedContactRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val senderPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""

    private val _sosState = MutableStateFlow<SOSState>(SOSState.Idle)
    val sosState: StateFlow<SOSState> = _sosState.asStateFlow()

    fun sendSOSAlert(userName: String, location: LatLng) {
        viewModelScope.launch {
            _sosState.value = SOSState.Sending

            try {
                val contactList = trustedContactRepository
                    .getTrustedContacts(userId)
                    .first() // take first emission and complete


                if (contactList.isEmpty()) {
                    _sosState.value = SOSState.Error("Add trusted contacts first")
                    return@launch
                }

                val (appUsers, nonAppUsers) = contactList.partition { it.isAppUser }

                // Save FCM notifications to Firestore for app users
                appUsers.forEach { contact ->
                    saveSOSAlert(contact, userName, location)
                }

                _sosState.value = SOSState.Success(
                    appNotificationSent = appUsers.size,
                    nonAppContacts = nonAppUsers.map { it.phoneNumber to it.name },
                    location = "${location.latitude},${location.longitude}",
                    message = buildSOSMessage(userName, location)
                )
            } catch (e: Exception) {
                _sosState.value = SOSState.Error(e.message ?: "Failed to send alerts")
            }
        }
    }

    private suspend fun saveSOSAlert(
        contact: TrustedContact,
        senderName: String,
        location: LatLng
    ) {
        val alertData = hashMapOf(
            "senderName" to senderName,
            "senderPhone" to senderPhone,
            "recipientPhone" to contact.phoneNumber,
            "location" to "${location.latitude}, ${location.longitude}",
            "locationUrl" to "https://maps.google.com/?q=${location.latitude},${location.longitude}",
            "timestamp" to System.currentTimeMillis(),
            "read" to false,
            "type" to "SOS"
        )

        firestore.collection("sosAlerts")
            .document(contact.phoneNumber)
            .collection("alerts")
            .add(alertData)
            .await()

        Log.d("SOSViewModel", "SOS alert saved for ${contact.phoneNumber}")
    }

    private fun buildSOSMessage(userName: String, location: LatLng): String {
        return "🚨 SOS from $userName!\n\nI need immediate help.\n\nLocation: https://maps.google.com/?q=${location.latitude},${location.longitude}"
    }

    fun resetState() {
        _sosState.value = SOSState.Idle
    }
}

sealed class SOSState {
    object Idle : SOSState()
    object Sending : SOSState()
    data class Success(
        val appNotificationSent: Int,
        val nonAppContacts: List<Pair<String, String>>,
        val location: String,
        val message: String
    ) : SOSState()
    data class Error(val message: String) : SOSState()
}