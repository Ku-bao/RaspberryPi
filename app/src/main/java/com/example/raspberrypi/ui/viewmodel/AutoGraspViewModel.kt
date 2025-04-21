package com.example.raspberrypi.ui.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raspberrypi.data.api.NetworkClient
import com.example.raspberrypi.data.model.AutoGraspData
import com.example.raspberrypi.data.model.ButtonCommand
import com.example.raspberrypi.data.model.ButtonIds
import com.example.raspberrypi.data.model.ControlData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoGraspViewModel : ViewModel() {
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming

    // 使用连接ViewModel的连接状态
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _isDetect = MutableStateFlow(false)
    val isDetect: StateFlow<Boolean> = _isDetect

    // 物体坐标数据
    private val _xCoordinate = MutableStateFlow("0.0")
    val xCoordinate: StateFlow<String> = _xCoordinate

    private val _yCoordinate = MutableStateFlow("0.0")
    val yCoordinate: StateFlow<String> = _yCoordinate

    private val _zCoordinate = MutableStateFlow("0.0")
    val zCoordinate: StateFlow<String> = _zCoordinate

    private val _distance = MutableStateFlow("0.0")
    val distance: StateFlow<String> = _distance

    private val _angle = MutableStateFlow("0")
    val angle: StateFlow<String> = _angle

    private val _action = MutableStateFlow(false)
    val action: StateFlow<Boolean> = _action

    fun toggleDetect() {
        if (_isDetect.value) {
            stopAutoGrasp()
        } else {
            startAutoGrasp()
        }
        _isDetect.value = !_isDetect.value
    }

    fun toggleStreaming() {
        if (_isStreaming.value) {
            closeVideo()
        } else {
//            startAutoGrasp()
        }
        _isStreaming.value = !_isStreaming.value
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

    /**
     * 开始自动抓取
     */
    private fun startAutoGrasp() {
        _action.value = true
        viewModelScope.launch {
            try {
                val response = NetworkClient.raspberryPiService.autoGrasp(
                    getAutoGraspData()
                )
                _isConnected.value = response.isSuccessful
                Log.d("AutoGraspViewModel", "发送抓取命令成功: ${response.isSuccessful}")
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("AutoGraspViewModel", "发送按钮命令失败: ${e.message}")
            }
        }
    }

    /**
     * 停止自动抓取
     */
   private fun stopAutoGrasp() {
        _action.value = false
        viewModelScope.launch {
            try {
                val response = NetworkClient.raspberryPiService.autoGrasp(
                    getAutoGraspData()
                )
                _isConnected.value = response.isSuccessful
                Log.d("AutoGraspViewModel", "发送停抓取命令成功: ${response.isSuccessful}")
            } catch (e: Exception) {
                _isConnected.value = false
                Log.e("AutoGraspViewModel", "发送停止抓取命令失败: ${e.message}")
            }
        }
    }

    /**
     * 更新检测到的物体坐标
     */
    @SuppressLint("DefaultLocale")
    fun updateObjectCoordinates(x: Double, y: Double, z: Double, dist: Double, ang: Int) {
        _xCoordinate.value = String.format("%.1f", x)
        _yCoordinate.value = String.format("%.1f", y)
        _zCoordinate.value = String.format("%.1f", z)
        _distance.value = String.format("%.1f", dist)
        _angle.value = String.format("%d°", ang)

    }

    private fun getAutoGraspData(): AutoGraspData {
        return AutoGraspData(
            x = _xCoordinate.value.toFloat(),
            y = _yCoordinate.value.toFloat(),
            z = _zCoordinate.value.toFloat(),
            distance = _distance.value.toFloat(),
            angle = _angle.value.toInt(),
            action = _action.value
        )
    }
}