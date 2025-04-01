package com.tcgcollector.ui.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TCGWebViewScreen(
    url: String = "https://www.tcgcollector.com",
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var webView: WebView? = remember { null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TCG Collector") },
                actions = {
                    IconButton(onClick = { webView?.reload() }) {
                        Text("⟳")
                    }
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webView?.url))
                        context.startActivity(intent)
                    }) {
                        Text("🌐")
                    }
                    IconButton(onClick = onClose) {
                        Text("✕")
                    }
                }
            )
        }
    ) { paddingValues ->
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onClose()
        }
    }
}
