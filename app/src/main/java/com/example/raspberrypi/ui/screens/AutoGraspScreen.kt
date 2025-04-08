package com.example.raspberrypi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.raspberrypi.ui.viewmodel.AutoGraspViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoGraspScreen(
    navController: NavController,
    viewModel: AutoGraspViewModel = viewModel()
) {
    val isStreaming by viewModel.isStreaming.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    // 模拟的动态数据
    val xCoordinate = remember { mutableStateOf("0.0") }
    val yCoordinate = remember { mutableStateOf("0.0") }
    val zCoordinate = remember { mutableStateOf("0.0") }

    val distance = remember { mutableStateOf("10.0") }
    val angle = remember { mutableStateOf("45°") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("自动抓取模式") },
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
                .padding(paddingValues),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 第一部分：视频流
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .padding(start = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {

                }
            }

            // 第二部分：数据展示和按钮区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 显示3D坐标
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("3D坐标: (${xCoordinate.value}, ${yCoordinate.value}, ${zCoordinate.value})")
                    }

                    // 显示需要移动的距离
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("移动距离: ${distance.value} cm")
                    }

                    // 显示偏移角度
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("偏移角度: ${angle.value}")
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    // 切换按钮
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
                        Text(if (isStreaming) "停止自动抓取" else "开始自动抓取")
                    }
                }
            }
        }
    }
}
