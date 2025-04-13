package com.example.raspberrypi.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raspberrypi.data.api.NetworkClient
import com.example.raspberrypi.data.model.ButtonCommand
import com.example.raspberrypi.data.model.ButtonIds
import com.example.raspberrypi.data.model.ControlData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ControlViewModel : ViewModel() {
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming

    private val _angle = MutableStateFlow("45°")
    val angle: StateFlow<String> = _angle

    // 使用连接ViewModel的连接状态
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    /**
     * 发送摇杆控制数据
     */
    fun sendControlData(x: Float, y: Float, speed: Float = 1.0f) {
        viewModelScope.launch {
            try {
                val response = NetworkClient.raspberryPiService.sendControlData(
                    ControlData(x, y, speed)
                )
                _isConnected.value = response.isSuccessful
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("ControlViewModel", "发送控制数据失败: ${e.message}")
            }
        }
    }
    
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
                Log.d("ControlViewModel", "发送按钮命令: $buttonId, 状态: $isPressed, 成功: ${response.isSuccessful}")
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("ControlViewModel", "发送按钮命令失败: ${e.message}")
            }
        }
    }

    fun checkConnection() {
        viewModelScope.launch {
            try {
                val isConnected = NetworkClient.checkConnection()
                _isConnected.value = isConnected
                Log.d("AutoGraspViewModel", "连接状态检查: $isConnected")
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("AutoGraspViewModel", "连接状态检查失败: ${e.message}")
            }
        }
    }

    fun closeVideo() {
        viewModelScope.launch {
            try {
                val response = NetworkClient.raspberryPiService.stopVideo()
                _isConnected.value = response.isSuccessful
                Log.d("AutoGraspViewModel", "发送停止命令成功: ${response.isSuccessful}")
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("AutoGraspViewModel", "发送按钮命令失败: ${e.message}")
            }
        }
    }

    fun toggleStreaming() {
        if (_isStreaming.value) {
            endDectect()
        } else {
            startDectect()
        }
        _isStreaming.value = !_isStreaming.value
    }

    /**
     * 抓取
     */
    fun Grasp() {
        sendButtonCommand(ButtonIds.BUTTON_GRASP)
    }
    /**
     * 模式
     */
    fun setModel1() {
        sendButtonCommand(ButtonIds.BUTTON_MODEL1)
    }

    fun setModel2() {
        sendButtonCommand(ButtonIds.BUTTON_MODEL2)
    }

    fun setModel3() {
        sendButtonCommand(ButtonIds.BUTTON_MODEL3)
    }

    fun turnLeft() {
        sendButtonCommand(ButtonIds.BUTTON_LEFT)
    }

    fun turnRight() {
        sendButtonCommand(ButtonIds.BUTTON_RIGHT)
    }

    /**
     * 开关目标检测
     */
    fun startDectect() {
        sendButtonCommand(ButtonIds.DETECT, true)
    }

    fun endDectect() {
        sendButtonCommand(ButtonIds.DETECT, false)
    }
} 