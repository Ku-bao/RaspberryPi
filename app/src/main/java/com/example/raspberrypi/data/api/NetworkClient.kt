package com.example.raspberrypi.data.api

import android.util.Log
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private var ipAddress = "192.168.10.126"
    private var port = "3160"
    private var protocol = "http"

    private lateinit var retrofit: Retrofit
    lateinit var raspberryPiService: RaspberryPiService
        private set

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        buildRetrofit()
    }

    private fun getBaseUrlInternal(): String {
        return "$protocol://$ipAddress:$port/"
    }

    fun getIpAddress():String {
        return ipAddress
    }

    fun updateIpAddress(newIp: String) {
        ipAddress = newIp
        buildRetrofit()
    }

    fun updatePort(newPort: String) {
        port = newPort
        buildRetrofit()
    }

    fun updateProtocol(newProtocol: String) {
        protocol = newProtocol
        buildRetrofit()
    }

    suspend fun checkConnection(): Boolean {
        return try {
            val response = raspberryPiService.ping()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NetworkClient", "连接检查失败: ${e.message}")
            false
        }
    }

    private fun buildRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrlInternal()) // ✅ 使用动态构造的 URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        raspberryPiService = retrofit.create(RaspberryPiService::class.java)
    }
}
