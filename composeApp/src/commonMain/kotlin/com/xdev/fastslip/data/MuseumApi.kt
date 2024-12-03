package com.xdev.fastslip.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.utils.io.CancellationException

interface MuseumApi {
    suspend fun getData(): List<com.xdev.fastslip.data.MuseumObject>
}

class KtorMuseumApi(private val client: HttpClient) : com.xdev.fastslip.data.MuseumApi {
    companion object {
        private const val API_URL =
            "https://raw.githubusercontent.com/Kotlin/KMP-App-Template/main/list.json"
    }

    override suspend fun getData(): List<com.xdev.fastslip.data.MuseumObject> {
        return try {
            client.get(com.xdev.fastslip.data.KtorMuseumApi.Companion.API_URL).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }
}
