package com.example.raspberrypi.ui.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raspberrypi.data.api.NetworkClient
import com.example.raspberrypi.data.model.ButtonCommand
import com.example.raspberrypi.data.model.ButtonIds
import com.example.raspberrypi.data.model.ControlData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoGraspViewModel : ViewModel() {
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // 物体坐标数据
    private val _xCoordinate = MutableStateFlow("0.0")
    val xCoordinate: StateFlow<String> = _xCoordinate

    private val _yCoordinate = MutableStateFlow("0.0")
    val yCoordinate: StateFlow<String> = _yCoordinate

    private val _zCoordinate = MutableStateFlow("0.0")
    val zCoordinate: StateFlow<String> = _zCoordinate

    private val _distance = MutableStateFlow("10.0")
    val distance: StateFlow<String> = _distance

    private val _angle = MutableStateFlow("45°")
    val angle: StateFlow<String> = _angle


    /**
     * 发送按钮命令
     * @param buttonId 按钮ID
     * @param isPressed 按钮状态，默认为按下(true)
     */
    fun sendButtonCommand(buttonId: String, isPressed: Boolean = true) {
        viewModelScope.launch {
            try {
                val response = NetworkClient.raspberryPiService.sendButtonCommand(
                    ButtonCommand(
                        buttonId = buttonId,
                        buttonAction = isPressed
                    )
                )
                _isConnected.value = response.isSuccessful
                Log.d("AutoGraspViewModel", "发送按钮命令: $buttonId, 状态: $isPressed, 成功: ${response.isSuccessful}")
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("AutoGraspViewModel", "发送按钮命令失败: ${e.message}")
            }
        }
    }

    fun toggleStreaming() {
        if (_isStreaming.value) {
            stopAutoGrasp()
        } else {
            startAutoGrasp()
        }
        _isStreaming.value = !_isStreaming.value
    }

    /**
     * 开始自动抓取
     */
    fun startAutoGrasp() {
        sendButtonCommand(ButtonIds.AUTO_GRASP, true)
    }

    /**
     * 停止自动抓取
     */
    fun stopAutoGrasp() {
        sendButtonCommand(ButtonIds.AUTO_GRASP, false)
    }

    /**
     * 更新检测到的物体坐标
     */
    @SuppressLint("DefaultLocale")
    fun updateObjectCoordinates(x: Double, y: Double, z: Double, dist: Double, ang: Double) {
        _xCoordinate.value = String.format("%.2f", x)
        _yCoordinate.value = String.format("%.2f", y)
        _zCoordinate.value = String.format("%.2f", z)
        _distance.value = String.format("%.2f", dist)
        _angle.value = String.format("%.1f°", ang)
    }
}