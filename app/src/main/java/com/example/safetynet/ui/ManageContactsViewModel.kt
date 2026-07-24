package com.example.safetynet.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.TrustedContact
import com.example.safetynet.data.TrustedContactRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ManageContactsViewModel @Inject constructor(
    private val repository: TrustedContactRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    //private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }


    private val _contacts = MutableStateFlow<List<TrustedContact>>(emptyList())
    val contacts: StateFlow<List<TrustedContact>> = _contacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val currentUserId = getUserId()
        viewModelScope.launch {
            repository.getTrustedContacts(currentUserId).collect {
                _contacts.value = it
            }
        }
    }

    fun addContact(name: String, phone: String, relationship: String) {

        //temp
        val currentUserId = getUserId()
        if (currentUserId.isEmpty()) {
            Log.e("CONTACT_DEBUG", "User not logged in!")
            return
        }

        viewModelScope.launch {
            // Normalize the phone number (remove spaces, dashes, etc.)
            val normalizedPhone = phone.trim()
                .replace(Regex("\\s+"), "")
                .replace(Regex("[\\-()]"), "")

            val phoneWithCountryCode = if (normalizedPhone.startsWith("+")) {
                normalizedPhone
            } else if (normalizedPhone.length == 10) {
                "+91$normalizedPhone"
            } else {
                normalizedPhone
            }


//             check if this phone number exists in the app users collection
            val isAppUser = try {
                val query =firestore.collection("users")
                    .whereEqualTo("phoneNumber", phoneWithCountryCode)
                    .limit(1)
                    .get()
                    .await()

                !query.isEmpty
            } catch (e: Exception) {
                false
            }

            val contact = TrustedContact(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                name = name,
                phoneNumber = phoneWithCountryCode,
                relationship = relationship,
                isAppUser = isAppUser
            )

            repository.addContact(currentUserId, contact)
        }
    }

    fun deleteContact(contactId: String) {
        val currentUserId = getUserId()
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            repository.deleteContact(currentUserId, contactId)
        }
    }

}