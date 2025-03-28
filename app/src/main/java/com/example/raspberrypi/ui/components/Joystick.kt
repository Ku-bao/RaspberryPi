package com.example.raspberrypi.ui.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onDirectionChange: (Float, Float) -> Unit
) {
    val maxRadius = 70f
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    
    // 监听位置变化
    LaunchedEffect(currentPosition) {
        Log.d("Joystick", "Position updated: $currentPosition")
    }
    
    // 计算当前方向
    val distance = sqrt(currentPosition.x * currentPosition.x + currentPosition.y * currentPosition.y)
    val normalizedX = (currentPosition.x / maxRadius).coerceIn(-1f, 1f)
    val normalizedY = (currentPosition.y / maxRadius).coerceIn(-1f, 1f)
    
    // 当方向改变时通知父组件
    LaunchedEffect(normalizedX, normalizedY) {
        Log.d("Joystick", "Direction changed: x=$normalizedX, y=$normalizedY")
        onDirectionChange(normalizedX, normalizedY)
    }
    
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        Log.d("Joystick", "Drag started at: $offset")
                    },
                    onDrag = { change, dragAmount ->
                        Log.d("Joystick", "Dragging: change=$change, dragAmount=$dragAmount")
                        change.consume()
                        
                        // 直接使用拖动的增量来更新位置
                        val newX = currentPosition.x + dragAmount.x
                        val newY = currentPosition.y + dragAmount.y
                        val newDistance = sqrt(newX * newX + newY * newY)
                        
                        Log.d("Joystick", "New position: x=$newX, y=$newY, distance=$newDistance")
                        
                        if (newDistance <= maxRadius) {
                            currentPosition = Offset(newX, newY)
                            Log.d("Joystick", "Position updated to: $currentPosition")
                        } else {
                            // 如果超出最大半径，计算在圆上的点
                            val angle = atan2(newY, newX)
                            currentPosition = Offset(
                                cos(angle) * maxRadius,
                                sin(angle) * maxRadius
                            )
                            Log.d("Joystick", "Position limited to circle: ${currentPosition}")
                        }
                    },
                    onDragEnd = {
                        Log.d("Joystick", "Drag ended")
                        // 平滑回到中心
                        currentPosition = Offset.Zero
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // 摇杆头部
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .offset(x = currentPosition.x.dp, y = currentPosition.y.dp)
        )
    }
}

private fun Offset.getDistance(): Float {
    return kotlin.math.sqrt(x * x + y * y)
}

private fun Offset.getAngle(): Float {
    return kotlin.math.atan2(y, x)
} 