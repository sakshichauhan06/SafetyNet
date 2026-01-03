package com.example.safetynet.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import domain.IncidentType
import kotlin.math.exp
import com.example.safetynet.R
import com.example.safetynet.ui.theme.ColorLightBlue
import com.example.safetynet.ui.theme.ColorLightestBlue
import com.example.safetynet.ui.theme.ColorPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentDialog(
    onDismiss: () -> Unit,
    onSubmit: (incidentType: IncidentType, additionalDetails: String) -> Unit
) {

    var selectedIncident by remember { mutableStateOf<IncidentType?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var additionalDetails by remember { mutableStateOf("") }

    BasicAlertDialog (
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.85f)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column (
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.flag),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Report Incident",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        value = selectedIncident?.displayName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                        ),
                        placeholder = { Text("What happened?", color = Color.White, style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = ColorPrimary,
                            unfocusedContainerColor = ColorPrimary,
                            disabledContainerColor = ColorPrimary,

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                            .height(49.dp),
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White).exposedDropdownSize(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        IncidentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName, color = Color.Black, style = MaterialTheme.typography.bodySmall) },
                                onClick = {
                                    selectedIncident = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = additionalDetails,
                    onValueChange = { additionalDetails = it },
                    placeholder = {
                        Text("Additional details (optional)", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ColorLightestBlue,
                        unfocusedContainerColor = ColorLightestBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

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
                        onClick = { selectedIncident?.let { onSubmit(it, additionalDetails) } },
                        enabled = selectedIncident != null,
                        modifier = Modifier.weight(1f), // Buttons share width equally
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
                    ) {
                        Text("Submit", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }

            }
        }
    }

}


















