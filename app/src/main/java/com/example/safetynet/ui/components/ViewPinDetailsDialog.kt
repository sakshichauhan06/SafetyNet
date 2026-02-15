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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.safetynet.R
import com.example.safetynet.data.SafetyPin
import com.example.safetynet.domain.SeverityLevel
import com.example.safetynet.utils.TimeUtils
import com.example.safetynet.ui.theme.ColorCritical

// title, detailed desc., timestamp, circle: SEVERITY, Buttons: Cancel & Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPinDetailsDialog(
    pin: SafetyPin,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val noisePainter = painterResource(id = R.drawable.noise_texture)
    val (topColor, bottomColor) = getSecerityColorGradient(pin.severity)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            0.0f to topColor,
                            0.25f to bottomColor,
                            0.25f to MaterialTheme.colorScheme.surface,
                            1f to MaterialTheme.colorScheme.surface
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = pin.shortDescription,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Detailed Description
                    Text(
                        text = pin.detailedDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
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
                        // Cancel button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f), // Buttons share width equally
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(2.dp, Color(0xFF0A0A1C))
                        ) {
                            Text("Cancel", color = Color(0xFF0A0A1C), style = MaterialTheme.typography.labelMedium)
                        }

                        // Delete button
                        Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f), // Buttons share width equally
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorCritical)
                        ) {
                            Text("Delete", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
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

private fun getSecerityColorGradient(severity: SeverityLevel): Pair<Color, Color> {
    return when(severity) {
        SeverityLevel.RED -> {
            Color(0xFFFFA6A7) to Color(0xFFFFEDED)
        }
        SeverityLevel.ORANGE -> {
            Color(0xFFFFB78D) to Color(0xFFFFEDEB)
        }
        SeverityLevel.YELLOW -> {
            Color(0xFFFFDA8F) to Color(0xFFFFF4DF)
        }
        SeverityLevel.GREEN -> {
            Color(0xFFACFFC8) to Color(0xFFF1FFF5)
        }
    }
}