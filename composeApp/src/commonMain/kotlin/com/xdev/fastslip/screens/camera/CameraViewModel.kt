package com.xdev.fastslip.screens.camera

import androidx.lifecycle.ViewModel
import com.xdev.fastslip.camera.CameraHelper
import com.xdev.fastslip.utils.DeviceRotationChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel(
    //private val rotationChecker: DeviceRotationChecker,
) : ViewModel() {
    private val _qrCodeResult = MutableStateFlow("")
    val qrCodeResult: StateFlow<String> = _qrCodeResult.asStateFlow()

    private val _error = MutableStateFlow<Exception?>(null)
    val error: StateFlow<Exception?> = _error.asStateFlow()

    private val cameraHelper = CameraHelper()

    // Device rotation
//    val isPortrait: StateFlow<Boolean> = rotationChecker.isPortrait
//    val isLandscapeNormal: StateFlow<Boolean> = rotationChecker.isLandscapeNormal
//    val isLandscapeReverse: StateFlow<Boolean> = rotationChecker.isLandscapeReverse
}