package com.xdev.fastslip.screens.camera

import com.xdev.fastslip.screens.camera.components.QRCodeScanResultDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.xdev.fastslip.camera.CameraHelper
import com.xdev.fastslip.screens.camera.components.BackButton
import com.xdev.fastslip.screens.camera.components.QrCodeBox
import com.xdev.fastslip.utils.getScreenDimensions
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CameraScreen(navigateBack: () -> Unit) {
    val cameraViewModel = koinViewModel<CameraViewModel>()
    val cameraHelper = CameraHelper()

    // QR code detection
    var detectedQrCode by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // screen dimensions
    val screenDim = getScreenDimensions()
    val hInDp = screenDim.height
    val wInDp = screenDim.width
    val yOffset = (hInDp - wInDp) * 0.9f

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview with lower zIndex
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        ) {
            cameraHelper.CameraPreview(
                onQrCodeDetected = { qrCode ->
                    detectedQrCode = qrCode
                    showDialog = true
                },
                onError = { exception ->
                    exception.printStackTrace()
                }
            )
        }

        // QrCodeBox with higher zIndex to be above camera preview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            QrCodeBox()
        }

        BackButton(
            onClick = navigateBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .zIndex(2f)
                .offset(y = yOffset),
            backgroundColor = Color.White.copy(alpha = 0.3f),
            size = 60.dp,
            iconSize = 35.dp
        )

        if (showDialog) {
            QRCodeScanResultDialog(
                scanResult = detectedQrCode,
                isSuccess = true,
                onDismissRequest = {
                    showDialog = false
                },
                onScanAnother = {
                    showDialog = false
                },
                onAutoClose = {
                    showDialog = false
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraHelper.shutdown()
        }
    }
}