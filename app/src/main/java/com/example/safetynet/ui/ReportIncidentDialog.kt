package com.example.safetynet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(id = R.drawable.flag),
                contentDescription = "Flag Icon",
                modifier = Modifier.size(48.dp),
                colorFilter = null
            )
        },
        title = {
            Text(
                text = "Report Incident",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 21.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Column(
//                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .width(400.dp),
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        value = selectedIncident?.displayName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = null,
                        placeholder = {
                            Text(
                                "What happened?",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = TextFieldDefaults.colors(
                            // background colors
                            focusedContainerColor = ColorPrimary,
                            unfocusedContainerColor = ColorPrimary,
                            disabledContainerColor = ColorPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(ColorLightestBlue)
                            .exposedDropdownSize()
                            .heightIn(max = 250.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        IncidentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = type.displayName,
                                        style = MaterialTheme.typography.bodyMedium)
                                },
                                onClick = {
                                    selectedIncident = type
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                // Additional details optional
                TextField(
                    value = additionalDetails,
                    onValueChange = { additionalDetails = it },
                    label = {
                        Text(
                            text = "Additional details (optional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        // background colors
                        focusedContainerColor = ColorLightestBlue,
                        unfocusedContainerColor = ColorLightestBlue,
                        disabledContainerColor = ColorLightestBlue
                    ),
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedIncident?.let { onSubmit(it, additionalDetails) }
                },
                enabled = selectedIncident != null
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

}


















