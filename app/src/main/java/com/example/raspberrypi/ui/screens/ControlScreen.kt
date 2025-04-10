package com.example.raspberrypi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.raspberrypi.ui.components.Joystick
import com.example.raspberrypi.ui.viewmodel.ControlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    navController: NavController,
    viewModel: ControlViewModel = viewModel()
) {
    val isStreaming by viewModel.isStreaming.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val angle by viewModel.angle.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("遥控模式") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 第一部分：摇杆区域
            Box(
                modifier = Modifier
                    .fillMaxHeight() // 去掉固定宽度
                    .weight(0.5f), // 使用权重以平衡布局
                contentAlignment = Alignment.Center
            ) {
                Joystick(
                    onDirectionChange = { x, y ->
                        viewModel.sendControlData(x, y)
                    }
                )
            }

            // 第二部分：视频流区域
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    // TODO: 添加摄像头预览
                }
            }
            // 第三部分：按钮区域
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card (
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "偏移角度:",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 2.dp)
                            )
                            Text(
                                angle,
                                fontSize = 16.sp
                            )
                        }
                    }
                    // 三个功能按钮
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setModel1() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("按钮1")
                        }
                        Button(
                            onClick = { viewModel.setModel2() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("按钮2")
                        }
                        Button(
                            onClick = { viewModel.setModel3() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("按钮3")
                        }
                    }

                    // 开始抓取按钮
                    Button(
                        onClick = {viewModel.Grasp()},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("开始抓取")
                    }

                    // 开启检测按钮
                    Button(
                        onClick = { viewModel.toggleStreaming() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isStreaming)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            if (isStreaming) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isStreaming) "停止" else "开始",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(if (isStreaming) "停止识别" else "开始识别")
                    }
                }
            }
        }
    }
}
