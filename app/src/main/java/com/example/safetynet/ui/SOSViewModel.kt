package com.example.safetynet.ui

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class SOSViewModel @Inject constructor(
    private val trustedContactRepository: TrustedContactRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _sosState = MutableStateFlow<SOSState>(SOSState.Idle)
    val sosState: StateFlow<SOSState> = _sosState.asStateFlow()

    fun sendSOSAlert(userName: String, location: com.google.android.gms.maps.model.LatLng) {
        viewModelScope.launch {
            _sosState.value = SOSState.Sending

            try {
//                val contacts = trustedContactRepository
//                    .getTrustedContacts(userId)
//                    .collect { it } // as it returns Flow need to adjust is but later

                // for now, fetch once
                val contactList = firestore.collection("users")
                    .document(userId)
                    .collection("trustedContacts")
                    .get()
                    .await()
                    .toObjects(TrustedContact::class.java)

                if (contactList.isEmpty()) {
                    _sosState.value = SOSState.Error("Add trusted contacts first")
                    return@launch
                }

                val (appUsers, nonAppUsers) = contactList.partition { it.isAppUser }

                // Save FCM notifications to Firestore for app users
                appUsers.forEach { contact ->
                    savePendingNotification(contact, userName, location)
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

    private suspend fun savePendingNotification(
        contact: TrustedContact,
        senderName: String,
        location: LatLng
    ) {
        firestore.collection("pendingNotifications").add(mapOf(
            "recipientPhone" to contact.phoneNumber,
            "title" to "🚨 SOS from $senderName",
            "body" to "Your trusted contact needs help!",
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis(),
            "read" to false
        )).await()
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