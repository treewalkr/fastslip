package com.xdev.fastslip.camera

import android.Manifest
import android.content.Context
import android.util.Size as ImageSize
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

actual class CameraHelper {

    // Single-thread executor for image analysis
    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    // Initialize barcode scanner once
    private val barcodeScanner: BarcodeScanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    actual fun CameraPreview(
        onQrCodeDetected: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }

        when {
            permissionState.status.isGranted -> {
                CameraPreviewContent(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onQrCodeDetected = onQrCodeDetected,
                    onError = onError
                )
            }

            permissionState.status.shouldShowRationale -> {
                PermissionRationale {
                    coroutineScope.launch {
                        permissionState.launchPermissionRequest()
                    }
                }
            }

            else -> {
                PermissionDenied()
            }
        }
    }

    @Composable
    private fun PermissionRationale(onRequestPermission: () -> Unit) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Camera Permission Required") },
            text = { Text("This app needs camera access to scan QR codes.") },
            confirmButton = {
                TextButton(onClick = onRequestPermission) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = {}) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    private fun PermissionDenied() {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Camera permission is required to use this feature.")
        }
    }

    @Composable
    private fun CameraPreviewContent(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onQrCodeDetected: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
        var previewView by remember { mutableStateOf<PreviewView?>(null) }
        var camera by remember { mutableStateOf<Camera?>(null) }

        // Dynamically calculate half of the screen height in dp
        val displayMetrics = context.resources.displayMetrics
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density / 2
        val topHalfHeight = Dp(screenHeightDp)

        // Initialize CameraProvider
        LaunchedEffect(context) {
            try {
                val future = ProcessCameraProvider.getInstance(context)
                cameraProvider = future.get(5, TimeUnit.SECONDS) // Add timeout
            } catch (e: Exception) {
                onError(e)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topHalfHeight)
            //.aspectRatio(3f / 4f)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topHalfHeight),
                //.aspectRatio(3f / 4f),
                factory = { ctx ->
                    PreviewView(ctx).also { view ->
                        view.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                        view.scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewView = view
                    }
                },
                update = { view ->
                    val currentProvider = cameraProvider
                    if (currentProvider != null && camera == null) {
                        try {
                            // Unbind any existing use cases
                            currentProvider.unbindAll()

                            // Set up Preview with specific configuration
                            val preview = Preview.Builder()
                                .setTargetRotation(view.display.rotation)
                                .build()
                                .also {
                                    it.setSurfaceProvider(view.surfaceProvider)
                                }

                            // Set up ImageAnalysis with specific resolution
                            val resolutionSelector = ResolutionSelector.Builder()
                                .setResolutionStrategy(
                                    ResolutionStrategy(
                                        ImageSize(1280, 720),
                                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                                    )
                                )
                                .build()

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setResolutionSelector(resolutionSelector)
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setTargetRotation(view.display.rotation)
                                .build()
                                .also { analysis ->
                                    analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                        processImageProxy(
                                            barcodeScanner = barcodeScanner,
                                            imageProxy = imageProxy,
                                            onQrCodeDetected = onQrCodeDetected,
                                            onError = onError
                                        )
                                    }
                                }

                            // Select back camera
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            // Bind use cases to lifecycle
                            camera = currentProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            onError(exc)
                        }
                    }
                }
            )
        }

        // Cleanup when leaving composition
        DisposableEffect(Unit) {
            onDispose {
                cameraProvider?.unbindAll()
                camera = null
            }
        }
    }

    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy,
        onQrCodeDetected: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    barcode.rawValue?.let { qrCode ->
                        onQrCodeDetected(qrCode)
                    }
                }
            }
            .addOnFailureListener { exc ->
                onError(exc)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    // Call this method to clean up resources, e.g., in ViewModel or when no longer needed
    actual fun shutdown() {
        analysisExecutor.shutdown()
        barcodeScanner.close()
    }
}
