package com.example.safetynet.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safetynet.ui.theme.SafetyNetTheme
import domain.SeverityLevel

/**
 * Dialog to report a location.
 *
 * When a user clicks on a location, it will show a dialog
 * which will give the user to enter the following things:
 * Short Description, Detailed Description, Severity Level (red, orange, yellow, green)
 * Then 2 options: to Cancel or to Save it
 *
 */
@Composable
fun PinDetailsDialog(
    onDismiss: () -> Unit,
    onSave: (shortDesc: String, detailedDesc: String, severity: SeverityLevel) -> Unit
) {
    // state for the text fields
    var shortDescription by remember { mutableStateOf("") }
    var detailedDescription by remember { mutableStateOf("") }
//    var selectedSeverity by remember { mutableStateOf<SeverityLevel?>(null) }
    var selectedSeverityIndex by remember { mutableIntStateOf(0) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Location") },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                OutlinedTextField(
                    value = shortDescription,
                    onValueChange = { shortDescription = it },
                    label = { Text("Short Description") }
                )
                OutlinedTextField(
                    value = detailedDescription,
                    onValueChange = { detailedDescription = it },
                    label = { Text("Describe in Detail") }
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) {
                    SeverityLevel.entries.forEachIndexed { index, level ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = SeverityLevel.entries.size
                            ),
                            onClick = { selectedSeverityIndex = index },
                            selected = index == selectedSeverityIndex,
                            label = { Text(
                                text = level.displayName,
                                style = MaterialTheme.typography.bodySmall
                            ) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val severity = SeverityLevel.entries[selectedSeverityIndex]
                    onSave(shortDescription, detailedDescription, severity)
                },
                enabled = shortDescription.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun PinDetailsDialogPreview() {
    SafetyNetTheme {
        PinDetailsDialog(
            onDismiss = {},
            onSave = { _, _, _ -> }
        )
    }
}