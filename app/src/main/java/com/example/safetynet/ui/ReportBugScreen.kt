package com.example.safetynet.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.net.toUri

@Composable
fun ReportBugScreen(navController: NavController) {
    val context = LocalContext.current
    var bugDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Report Bug",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Found something wrong? Let us know so we can fix it!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bugDescription,
            onValueChange = { bugDescription = it },
            label = { Text("Describe the issue..") },
            modifier = Modifier.fillMaxWidth().height(200.dp),
            placeholder = { Text("e.g. The map isn't loading pins when I move the screen.") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:".toUri()
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("sakshi.chauhan17.developer@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Bug Report - SafetyNet App")
                    putExtra(Intent.EXTRA_TEXT, """
                        Issue Description:
                        $bugDescription
                        
                        --- System Info ---
                        Device: ${android.os.Build.MODEL}
                        Android Version: ${android.os.Build.VERSION.RELEASE}
                    """.trimIndent())
                }

                // Try to start the activity (with a check in case no email app exists)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Handle case where no email app is installed
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = bugDescription.isNotBlank()
        ) {
            Text("Send via Email")
        }
    }
}