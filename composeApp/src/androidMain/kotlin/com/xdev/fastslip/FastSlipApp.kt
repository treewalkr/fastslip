package com.xdev.fastslip

import android.app.Application
import com.xdev.fastslip.di.initKoin
import com.xdev.fastslip.di.repositoryModule
import com.xdev.fastslip.di.appModule

class FastSlipApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this, repositoryModule, appModule)
    }
}
