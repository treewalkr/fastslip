package com.xdev.fastslip.camera

import androidx.compose.runtime.Composable

actual class CameraHelper actual constructor() {
    @Composable
    actual fun CameraPreview(
        onQrCodeDetected: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
    }
}