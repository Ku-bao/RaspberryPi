package com.example.raspberrypi.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
@Composable
fun Joystick(
    onDirectionChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 获取 MaterialTheme 中的颜色
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val primaryColor = MaterialTheme.colorScheme.primary

    // 使用 AndroidView 来嵌入 JoystickView
    AndroidView(
        factory = {
            JoystickView(it).apply {
                // 设置摇杆的背景色和摇杆头的颜色
                setJoystickColors(primaryContainerColor.toArgb(), primaryColor.toArgb())

                // 设置方向变化的回调
                setOnDirectionChangeListener { x, y ->
                    onDirectionChange(x, y)
                }
            }
        },
        modifier = modifier
    )
}