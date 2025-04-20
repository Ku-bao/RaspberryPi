package com.example.raspberrypi.data.api

import com.example.raspberrypi.data.model.AutoGraspData
import com.example.raspberrypi.data.model.ButtonCommand
import com.example.raspberrypi.data.model.ControlData
import com.example.raspberrypi.data.model.ControlResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface RaspberryPiService {
    @POST("control")
    suspend fun sendControlData(@Body controlData: ControlData): Response<ControlResponse>

    @POST("autoGrasp")
    suspend fun autoGrasp(@Body autoGraspData: AutoGraspData):Response<Unit>

    @GET("stopVideo")
    suspend fun stopVideo(): Response<Unit>

    @POST("button")
    suspend fun sendButtonCommand(@Body buttonCommand: ButtonCommand): Response<Unit>

    @GET("ping")
    suspend fun ping(): Response<Unit>
}