package com.example.safetynet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import data.SafetyPin
import domain.SeverityLevel
import utils.TimeUtils

// title, detailed desc., timestamp, circle: SEVERITY, Buttons: Cancel & Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPinDetailsDialog(
    pin: SafetyPin,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    BasicAlertDialog (
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier.fillMaxWidth(0.85f)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = pin.shortDescription,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Detailed Description
                Text(
                    text = pin.detailedDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                // Timestamp
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "📅 ${TimeUtils.getRelativeTime(pin.timestamp)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Severity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(getSeverityColor(pin.severity))
                    )
                    Text(
                        text = pin.severity.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f), // Buttons share width equally
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color(0xFF0A0A1C))
                    ) {
                        Text("Cancel", color = Color(0xFF0A0A1C), style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f), // Buttons share width equally
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
                    ) {
                        Text("Delete", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }

            }
        }
    }
}

private fun getSeverityColor(severity: SeverityLevel): Color {
    return when(severity) {
        SeverityLevel.RED -> Color(0xFFEB2A34)
        SeverityLevel.ORANGE -> Color(0xFFE67E22)
        SeverityLevel.YELLOW -> Color(0xFFF1C40F)
        SeverityLevel.GREEN -> Color(0xFF2ECC71)
    }
}