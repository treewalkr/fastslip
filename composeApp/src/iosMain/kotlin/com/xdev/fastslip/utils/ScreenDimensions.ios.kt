package com.xdev.fastslip.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ScreenDimensionsIOS : ScreenDimensions {
    override val width: Dp
        get() = 0.dp

    override val height: Dp
        get() = 0.dp
}

actual fun getScreenDimensions(): ScreenDimensions {
    return ScreenDimensionsIOS()
}