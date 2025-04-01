package com.tcgcollector.ui.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcgcollector.camera.CardScanner

@Composable
fun NumberScanScreen(
    onNumberConfirmed: (String) -> Unit,
    onCancel: () -> Unit
) {
    var scannedNumber by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Step 2: Scan Card Number",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            CardScanner { text ->
                val match = Regex("\\b\\d{1,3}/\\d{1,3}\\b").find(text)
                if (match != null) {
                    scannedNumber = match.value
                }
            }
        }

        Text(
            text = "Detected: $scannedNumber",
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Button(
                onClick = { onNumberConfirmed(scannedNumber) },
                enabled = scannedNumber.isNotBlank()
            ) {
                Text("Confirm Number")
            }
        }
    }
}
