package com.tcgcollector.camera

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

@Composable
fun CardScanner(
    onTextScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalContext.current as LifecycleOwner)

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                processImageProxy(imageProxy, recognizer, onTextScanned)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner.value,
                    cameraSelector,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                Log.e("CardScanner", "Camera binding failed", e)
            }

        }, ContextCompat.getMainExecutor(ctx))

        previewView
    })
}

@SuppressLint("UnsafeOptInUsageError")
private fun processImageProxy(
    imageProxy: ImageProxy,
    recognizer: com.google.mlkit.vision.text.TextRecognizer,
    onTextScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val rawText = visionText.text
                if (rawText.isNotEmpty()) {
                    onTextScanned(rawText)
                }
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Text recognition failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
