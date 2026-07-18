package com.example.safetynet.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrustedContactRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getTrustedContacts(userId: String) = firestore
        .collection("users")
        .document(userId)
        .collection("trustedContacts")
        .snapshots()
        .map { it.toObjects<TrustedContact>() }

    suspend fun addContact(userId: String, contact: TrustedContact) {
        firestore.collection("users")
            .document(userId)
            .collection("trustedContacts")
            .document(contact.id)
            .set(contact)
            .await()
    }

    suspend fun deleteContact(userId: String, contactId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("trustedContacts")
            .document(contactId)
            .delete()
            .await()
    }
}