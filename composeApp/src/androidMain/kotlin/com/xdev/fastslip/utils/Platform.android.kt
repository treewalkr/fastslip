package com.xdev.fastslip.utils

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun isAndroid(): Boolean = true
actual fun isIos(): Boolean = false