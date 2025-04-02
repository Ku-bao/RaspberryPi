package com.example.raspberrypi.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class JoystickView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val maxRadius = 200f
    private var centerPoint = Offset(0f, 0f)
    private var currentPosition = Offset(0f, 0f)
    private var onDirectionChangeListener: ((Float, Float) -> Unit)? = null

    // 新增的颜色变量，用于控制摇杆的背景和摇杆头
    private var joystickBackgroundColor: Int = Color.GRAY
    private var joystickThumbColor: Int = Color.BLUE

    init {
        // 默认颜色设置
        joystickBackgroundColor = Color.parseColor("#E0E0E0") // 默认 primaryContainer 颜色
        joystickThumbColor = Color.parseColor("#2196F3") // 默认 primary 颜色
    }

    // 新增方法：设置摇杆的颜色
    fun setJoystickColors(backgroundColor: Int, thumbColor: Int) {
        joystickBackgroundColor = backgroundColor
        joystickThumbColor = thumbColor
        invalidate() // 更新视图
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()

        // 绘制摇杆背景
        paint.color = joystickBackgroundColor
        canvas?.drawCircle(width / 2f, height / 2f, maxRadius, paint)

        // 绘制摇杆头部
        paint.color = joystickThumbColor
        canvas?.drawCircle(
            width / 2f + currentPosition.x,
            height / 2f + currentPosition.y,
            70f,  // 摇杆头的大小
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                centerPoint = Offset(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = event.x - centerPoint.x
                val offsetY = event.y - centerPoint.y
                val distance = sqrt(offsetX * offsetX + offsetY * offsetY)

                // 控制摇杆不超过最大半径
                if (distance <= maxRadius) {
                    currentPosition = Offset(offsetX, offsetY)
                } else {
                    val angle = atan2(offsetY, offsetX)
                    currentPosition = Offset(
                        cos(angle) * maxRadius,
                        sin(angle) * maxRadius
                    )
                }

                // 调用方向变化的回调
                onDirectionChangeListener?.invoke(
                    currentPosition.x / maxRadius,
                    currentPosition.y / maxRadius
                )
                invalidate()  // 重新绘制
                return true
            }
            MotionEvent.ACTION_UP -> {
                currentPosition = Offset(0f, 0f)
                invalidate()  // 重新绘制
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setOnDirectionChangeListener(listener: (Float, Float) -> Unit) {
        onDirectionChangeListener = listener
    }

    // 存储坐标的辅助类
    data class Offset(val x: Float, val y: Float)
}
