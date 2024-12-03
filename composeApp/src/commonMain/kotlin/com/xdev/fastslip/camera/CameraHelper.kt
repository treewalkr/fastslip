package com.xdev.fastslip.camera

import androidx.compose.runtime.Composable

expect class CameraHelper() {
    @Composable
    fun CameraPreview(
        onQrCodeDetected: (String) -> Unit,
        onError: (Exception) -> Unit
    )

    fun shutdown()
}