package com.example.safetynet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetynet.data.TrustedContact
import com.example.safetynet.data.TrustedContactRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManageContactsViewModel @Inject constructor(
    private val repository: TrustedContactRepository
) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _contacts = MutableStateFlow<List<TrustedContact>>(emptyList())
    val contacts: StateFlow<List<TrustedContact>> = _contacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            repository.getTrustedContacts(userId).collect {
                _contacts.value = it
            }
        }
    }

    fun addContact(contact: TrustedContact) {
        viewModelScope.launch {
            repository.addContact(userId, contact)
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            repository.deleteContact(userId, contactId)
        }
    }

}