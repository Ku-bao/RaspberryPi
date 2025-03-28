package com.example.raspberrypi.data.model

data class ControlData(
    val x: Float,  // 摇杆X轴值 (-1.0 到 1.0)
    val y: Float,  // 摇杆Y轴值 (-1.0 到 1.0)
    val speed: Float = 1.0f  // 速度值 (0.0 到 1.0)
) 