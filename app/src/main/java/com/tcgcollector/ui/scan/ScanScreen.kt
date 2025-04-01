package com.tcgcollector.ui.scan

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tcgcollector.camera.CardScanner

@Composable
fun ScanScreen(
    onBack: () -> Unit
) {
    var scannedText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Scanning Card...",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            CardScanner { text ->
                scannedText = text
                Log.d("ScanScreen", "OCR Result: $text")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Scanned Text:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            scannedText,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Back to Dashboard")
        }
    }
}
