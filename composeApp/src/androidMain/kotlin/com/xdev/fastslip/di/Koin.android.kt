package com.xdev.fastslip.di

import com.xdev.fastslip.data.repository.BankAccountRepositoryImpl
import com.xdev.fastslip.domain.repository.BankAccountRepository
import com.xdev.fastslip.domain.usecase.GetBankAccountsUseCase
import com.xdev.fastslip.domain.usecase.UpdateBankAccountUseCase
import org.koin.dsl.module
import android.app.Application
import android.content.Context
import com.xdev.fastslip.utils.AndroidDeviceRotationChecker
import com.xdev.fastslip.utils.DeviceRotationChecker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module

val repositoryModule = module {
    single<BankAccountRepository> { BankAccountRepositoryImpl(get()) }
    factory { GetBankAccountsUseCase(get()) }
    factory { UpdateBankAccountUseCase(get()) }
}

val appModule = module {
    single<DeviceRotationChecker> { AndroidDeviceRotationChecker(get()) }
}

fun initKoin(app: Application, vararg platformModules: Module) {
    startKoin {
        androidContext(app) // Pass the Android context to Koin
        modules(
            dataModule,
            viewModelModule,
            *platformModules // Include any platform-specific modules
        )
    }
}