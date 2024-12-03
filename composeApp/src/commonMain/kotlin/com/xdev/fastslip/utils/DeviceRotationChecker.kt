package com.xdev.fastslip.utils

import kotlinx.coroutines.flow.StateFlow

interface DeviceRotationChecker {
    val isPortrait: StateFlow<Boolean>
    val isLandscapeNormal: StateFlow<Boolean>
    val isLandscapeReverse: StateFlow<Boolean>

    fun unregister()
}