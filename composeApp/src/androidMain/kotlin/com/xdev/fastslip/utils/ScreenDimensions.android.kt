package com.xdev.fastslip.utils

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class AndroidScreenDimensions : ScreenDimensions {
    override val width: Dp
        get() = (Resources.getSystem().displayMetrics.widthPixels /
                Resources.getSystem().displayMetrics.density).dp

    override val height: Dp
        get() = (Resources.getSystem().displayMetrics.heightPixels /
                Resources.getSystem().displayMetrics.density).dp
}

actual fun getScreenDimensions(): ScreenDimensions {
    return AndroidScreenDimensions()
}
