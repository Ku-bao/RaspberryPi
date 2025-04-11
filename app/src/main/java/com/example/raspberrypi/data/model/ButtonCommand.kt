package com.example.raspberrypi.data.model

import com.google.gson.annotations.SerializedName

/**
 * 按钮命令数据类，用于发送按钮相关的控制指令
 */
data class ButtonCommand(
    // 按钮标识符
    @SerializedName("button_id")
    val buttonId: String,
    
    // 按钮动作状态（按下/释放）
    @SerializedName("button_action")
    val buttonAction: Boolean = true
) 