package com.tcgcollector.ui.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcgcollector.camera.CardScanner

@Composable
fun NameScanScreen(
    onNameConfirmed: (String) -> Unit,
    onCancel: () -> Unit
) {
    var scannedName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Step 1: Scan Card Name",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            CardScanner { text ->
                // Filter for shortest line (likely card name)
                val candidate = text
                    .split("\n")
                    .map { it.trim() }
                    .firstOrNull { it.matches(Regex("^[A-Z][a-zA-Z0-9\\s-]{2,}$")) }

                if (!candidate.isNullOrEmpty()) {
                    scannedName = candidate
                }
            }
        }

        Text(
            text = "Detected: $scannedName",
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Button(
                onClick = { onNameConfirmed(scannedName) },
                enabled = scannedName.isNotBlank()
            ) {
                Text("Confirm Name")
            }
        }
    }
}
