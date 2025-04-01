package com.tcgcollector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tcgcollector.ui.theme.TCGCollectorScannerTheme
import com.tcgcollector.ui.scan.ScanScreen
import com.tcgcollector.ui.scan.NameScanScreen
import com.tcgcollector.ui.scan.NumberScanScreen
import com.tcgcollector.ui.scan.ConfirmCardMatchScreen
import com.tcgcollector.data.network.MockCardService
import com.tcgcollector.ui.auth.LoginSelectorScreen
import com.tcgcollector.ui.auth.LoginScreen
import com.tcgcollector.ui.webview.TCGWebViewScreen


class MainActivity : ComponentActivity() {
    var screenState by remember { mutableStateOf("login_selector") }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission()

        setContent {
            TCGCollectorScannerTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    private fun requestCameraPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 10)
        }
    }
}

@Composable
fun MainScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        LoginScreen(
            username = username,
            password = password,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLoginClick = {
                // TODO: Add TCG Collector API auth
                isLoggedIn = true
            }
        )
    } else {
        Dashboard()
    }
}

@Composable
fun LoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

@Composable
fun Dashboard() {

    var scannedName by remember { mutableStateOf("") }
    var scannedNumber by remember { mutableStateOf("") }

    var returnToConfirmFromName by remember { mutableStateOf(false) }
    var returnToConfirmFromNumber by remember { mutableStateOf(false) }

    when (screenState) {
        "dashboard" -> {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Welcome to TCG Collector Scanner!", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { screenState = "scan_name" }) {
                    Text("Start Guided Scan")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { screenState = "webview" }) {
                    Text("Open TCG Collector Website")
                }
            }
        }
        "webview" -> {
            TCGWebViewScreen(
                onClose = { screenState = "dashboard" }
            )
        }
        "webview_login" -> {
            TCGWebViewScreen(
                url = "https://www.tcgcollector.com/account/sign-in",
                onClose = { screenState = "dashboard" } // after login
            )
        }

        "api_login" -> {
            LoginScreen(
                onLoginSuccess = { token ->
                    println("API token: $token")
                    screenState = "dashboard"
                },
                onCancel = { screenState = "login_selector" }
            )
        }
        "scan_name" -> {
            NameScanScreen(
                onNameConfirmed = {
                    scannedName = it
                    screenState = if (returnToConfirmFromName) {
                        returnToConfirmFromName = false
                        "confirm"
                    } else {
                        "scan_number"
                    }
                },
                onCancel = {
                    returnToConfirmFromName = false
                    screenState = "dashboard"
                }
            )
        }

        "scan_number" -> {
            NumberScanScreen(
                onNumberConfirmed = {
                    scannedNumber = it
                    screenState = if (returnToConfirmFromNumber) {
                        returnToConfirmFromNumber = false
                        "confirm"
                    } else {
                        "confirm"
                    }
                },
                onCancel = {
                    returnToConfirmFromNumber = false
                    screenState = "dashboard"
                }
            )
        }

        "confirm" -> {
            ConfirmCardMatchScreen(
                cardName = scannedName,
                cardNumber = scannedNumber,
                onSubmit = { action, quantity, variant, cardId ->
                    println("User selected: $action $quantity x $variant of card ID $cardId")
                    screenState = "dashboard"
                },
                onCancel = { screenState = "dashboard" },
                onBackToName = {
                    returnToConfirmFromName = true
                    screenState = "scan_name"
                },
                onBackToNumber = {
                    returnToConfirmFromNumber = true
                    screenState = "scan_number"
                }
            )
        }
    }
}

