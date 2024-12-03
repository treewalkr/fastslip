package com.xdev.fastslip.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Surface
import android.view.WindowManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidDeviceRotationChecker(private val context: Context) : DeviceRotationChecker {

    private val _isPortrait = MutableStateFlow(isPortrait())
    override val isPortrait: StateFlow<Boolean> = _isPortrait

    private val _isLandscapeNormal = MutableStateFlow(isLandscapeNormal())
    override val isLandscapeNormal: StateFlow<Boolean> = _isLandscapeNormal

    private val _isLandscapeReverse = MutableStateFlow(isLandscapeReverse())
    override val isLandscapeReverse: StateFlow<Boolean> = _isLandscapeReverse

    private val rotationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Update flows on configuration change
            _isPortrait.value = isPortrait()
            _isLandscapeNormal.value = isLandscapeNormal()
            _isLandscapeReverse.value = isLandscapeReverse()
        }
    }

    init {
        // Register for configuration changes
        context.registerReceiver(
            rotationReceiver,
            IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        )
    }

    private fun getScreenRotation(): Int {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        return display.rotation
    }

    private fun isPortrait(): Boolean {
        return getScreenRotation() == Surface.ROTATION_0 || getScreenRotation() == Surface.ROTATION_180
    }

    private fun isLandscapeNormal(): Boolean {
        return getScreenRotation() == Surface.ROTATION_90
    }

    private fun isLandscapeReverse(): Boolean {
        return getScreenRotation() == Surface.ROTATION_270
    }

    override fun unregister() {
        context.unregisterReceiver(rotationReceiver)
    }
}