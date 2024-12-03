package com.xdev.fastslip.utils

import androidx.compose.ui.unit.Dp

interface ScreenDimensions {
    val width: Dp
    val height: Dp
}

expect fun getScreenDimensions(): ScreenDimensions