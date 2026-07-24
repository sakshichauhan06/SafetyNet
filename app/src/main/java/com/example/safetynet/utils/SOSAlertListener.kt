package com.example.safetynet.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.safetynet.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SOSAlertListener @Inject constructor(
    private val context: Context
) {

    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    fun startListening() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val myPhone = currentUser.phoneNumber

        // for email auth, phoneNumber might be null - use the one from firestore profile
        if (myPhone.isNullOrBlank()) {
            // fetch phone from Firestore user doc
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    val phone = doc.getString("phoneNumber")
                    if (!phone.isNullOrBlank()) {
                        startListeningForPhone(phone)
                    }
                }
            return
        }

        startListeningForPhone(myPhone)
    }

    private fun startListeningForPhone(phoneNumber: String) {
        Log.d("SOS_ALERT", "Starting listener for phone: $phoneNumber")

        listenerRegistration = firestore.collection("sosAlerts")
            .document(phoneNumber)
            .collection("alerts")
            .whereEqualTo("read", false)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("SOS_ALERT", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { change ->
                    if (change.type == DocumentChange.Type.ADDED) {
                        val data = change.document.data
                        val senderName = data["senderName"] as? String ?: "Someone"
                        val location = data["location"] as? String ?: "Unknown"
                        val senderPhone = data["senderPhone"] as? String ?: ""

                        showSOSNotification(senderName, location, senderPhone)

                        // mark as rad so we don't show again
                        change.document.reference.update("read", true)
                    }
                }
            }
    }

    private fun showSOSNotification(senderName: String, location: String, senderPhone: String) {
        val channelId = "sos_alerts"
        val notificationId = System.currentTimeMillis().toInt()

        // Create notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SOS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency alerts from trusted contacts"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open app when tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("sos_alert", true)
            putExtra("location", location)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🚨 SOS Alert from $senderName")
            .setContentText("Needs help at: $location")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)

        Log.d("SOS_ALERT", "Notification shown for $senderName")
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

}