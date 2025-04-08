package com.example.raspberrypi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raspberrypi.data.api.NetworkClient
import com.example.raspberrypi.data.model.ControlData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoGraspViewModel : ViewModel() {
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    fun sendControlData(x: Float, y: Float, speed: Float = 1.0f) {
        viewModelScope.launch {
            try {
                val response = NetworkClient.raspberryPiService.sendControlData(
                    ControlData(x, y, speed)
                )
                _isConnected.value = response.isSuccessful
            } catch (e: Exception) {
                _isConnected.value = false
            }
        }
    }

    fun toggleStreaming() {
        _isStreaming.value = !_isStreaming.value
    }
}