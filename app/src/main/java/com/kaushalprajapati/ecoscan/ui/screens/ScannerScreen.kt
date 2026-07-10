package com.kaushalprajapati.ecoscan.ui.screens

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.camera.core.ImageAnalysis
import com.kaushalprajapati.ecoscan.util.BarcodeAnalyzer

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onBarcodeDetected: (String) -> Unit,
    onUseSampleBarcode: (String) -> Unit,
    sampleBarcodes: List<String>,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            CameraPreviewWithAnalysis(onBarcodeDetected = onBarcodeDetected)
            ScannerOverlay()
        } else {
            PermissionRequest(
                onRequest = { cameraPermissionState.launchPermissionRequest() },
                sampleBarcodes = sampleBarcodes,
                onUseSampleBarcode = onUseSampleBarcode
            )
        }
    }
}

@Composable
private fun CameraPreviewWithAnalysis(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context),
                        BarcodeAnalyzer(onBarcodeDetected)
                    )
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }
    }
}

@Composable
private fun ScannerOverlay() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Transparent)
        )
        Text(
            text = "Align barcode within the frame",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}

@Composable
private fun PermissionRequest(
    onRequest: () -> Unit,
    sampleBarcodes: List<String>,
    onUseSampleBarcode: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = null, modifier = Modifier.size(48.dp))
        Text(
            "Camera access is needed to scan barcodes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Button(onClick = onRequest) { Text("Grant Camera Permission") }

        if (sampleBarcodes.isNotEmpty()) {
            Text(
                "…or try a sample product instead:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            sampleBarcodes.take(3).forEach { code ->
                Button(
                    onClick = { onUseSampleBarcode(code) },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) { Text(code) }
            }
        }
    }
}
