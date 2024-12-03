package com.xdev.fastslip.utils

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun isAndroid(): Boolean
expect fun isIos(): Boolean