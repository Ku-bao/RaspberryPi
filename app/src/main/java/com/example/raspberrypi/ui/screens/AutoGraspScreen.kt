package com.example.raspberrypi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.raspberrypi.ui.viewmodel.AutoGraspViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoGraspScreen(
    navController: NavController,
    viewModel: AutoGraspViewModel = viewModel()
) {
    val isStreaming by viewModel.isStreaming.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    val xCoordinate by viewModel.xCoordinate.collectAsState()
    val yCoordinate by viewModel.yCoordinate.collectAsState()
    val zCoordinate by viewModel.zCoordinate.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val angle by viewModel.angle.collectAsState()
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
        // 主内容使用Row来横向排列，更适合横屏
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 左侧：视频流区域（正方形）
            Box(
                modifier = Modifier
                    .weight(0.4f) // 减小视频区域比例
                    .aspectRatio(1f) // 保持1:1比例
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 视频流内容将在这里显示
                if (!isStreaming) {
                    Text(
                        text = "视频流预览区域",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // 右侧：信息和控制区域
            Column(
                modifier = Modifier
                    .weight(0.6f) // 增加右侧区域比例
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 物体信息卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "目标物体信息",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // 3D坐标
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "3D坐标:",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                "(${xCoordinate}, ${yCoordinate}, ${zCoordinate})",
                                fontSize = 16.sp
                            )
                        }
                        
                        // 移动距离
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "移动距离:",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                "${distance} cm",
                                fontSize = 16.sp
                            )
                        }
                        
                        // 偏移角度
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "偏移角度:",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                angle,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 控制按钮
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
                    Text(
                        if (isStreaming) "停止自动抓取" else "开始自动抓取",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
