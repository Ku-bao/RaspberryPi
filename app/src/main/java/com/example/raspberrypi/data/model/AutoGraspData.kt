package com.example.raspberrypi.data.model

data class AutoGraspData(
    val x: Float,
    val y: Float,
    val z: Float,
    val distance: Float,
    val angle: Int,
    val action: Boolean
)
