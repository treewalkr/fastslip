package com.xdev.fastslip.di

import com.xdev.fastslip.data.InMemoryMuseumStorage
import com.xdev.fastslip.data.MuseumRepository
import com.xdev.fastslip.data.MuseumStorage
import com.xdev.fastslip.screens.detail.DetailViewModel
import com.xdev.fastslip.screens.list.ListViewModel
import com.xdev.fastslip.screens.camera.CameraViewModel
import com.xdev.fastslip.screens.home.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
        }
    }

    single<com.xdev.fastslip.data.MuseumApi> { com.xdev.fastslip.data.KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }
}

val viewModelModule = module {
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
    factoryOf(::CameraViewModel)
    factoryOf(::HomeViewModel)
}

fun initKoin(vararg platformModules: Module) {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
            *platformModules // Include any platform-specific modules
        )
    }
}
