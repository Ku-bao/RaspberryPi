package com.example.raspberrypi.data.api

import com.example.raspberrypi.data.model.ControlData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface RaspberryPiService {
    @POST("control")
    suspend fun sendControlData(@Body controlData: ControlData): Response<Unit>
    
    @Streaming
    @GET("video")
    suspend fun getVideoStream(): Response<ResponseBody>
} 