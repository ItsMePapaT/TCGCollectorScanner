package com.tcgcollector.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginSelectorScreen(
    onWebLogin: () -> Unit,
    onApiLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Login Type", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onWebLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Log In via WebView")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onApiLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Log In via TCG API (Coming Soon)")
        }
    }
}
