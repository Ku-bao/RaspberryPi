package com.example.raspberrypi.ui.screens

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.raspberrypi.data.api.NetworkClient
import com.example.raspberrypi.ui.viewmodel.AutoGraspViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoGraspScreen(
    navController: NavController,
    viewModel: AutoGraspViewModel = viewModel()
) {
    val isStreaming by viewModel.isStreaming.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isDetect by viewModel.isDetect.collectAsState()

    val xCoordinate by viewModel.xCoordinate.collectAsState()
    val yCoordinate by viewModel.yCoordinate.collectAsState()
    val zCoordinate by viewModel.zCoordinate.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val angle by viewModel.angle.collectAsState()

    // 控制弹窗显示的状态
    var showDialog by remember { mutableStateOf(false) }

    // 需要设置的坐标和角度的初始值
    var newX by remember { mutableStateOf(xCoordinate) }
    var newY by remember { mutableStateOf(yCoordinate) }
    var newZ by remember { mutableStateOf(zCoordinate) }
    var newDistance by remember { mutableStateOf(distance) }
    var newAngle by remember { mutableStateOf(angle) }

    val webView = remember {
        WebView(navController.context).apply {
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webChromeClient = WebChromeClient()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkConnection()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto-Capture Mode") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.closeVideo()
                        navController.navigateUp()
                    }) {
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
                if (isConnected and isStreaming) {
                    val videoUrl =
                        "http://${NetworkClient.getIpAddress()}:${NetworkClient.getPort()}/video"
                    AndroidView(
                        factory = { webView },
                        update = {
                            if (isStreaming) {
                                if (it.url != videoUrl) {
                                    it.loadUrl(videoUrl)
                                }
                            } else {
                                if (it.url != "about:blank") {
                                    it.loadUrl("about:blank")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Video stream preview area",
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
                    Row(
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp).weight(2.5f)
                        ) {
                            Text(
                                text = "Target object information",
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
                                    text = "3D coordinates:",
                                    fontWeight = FontWeight.Medium,
                                    // modifier = Modifier.width(90.dp)
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
                                    text = "Moving distance:",
                                    fontWeight = FontWeight.Medium,
                                    // modifier = Modifier.width(90.dp)
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
                                    text = "Offset angle:",
                                    fontWeight = FontWeight.Medium,
                                    // modifier = Modifier.width(90.dp)
                                )
                                Text(
                                    angle,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(top = 112.dp, end = 12.dp)
                                .weight(1f),
                        ) {
                            Button(
                                onClick = { showDialog = true },
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text("Set Data")
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 自动抓取切换按钮（左边）
                    Button(
                        onClick = { viewModel.toggleDetect() },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDetect)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            if (isDetect) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isDetect) "停止" else "开始",
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            if (isDetect) "Stop Program" else "Start Program",
                            fontSize = 15.sp
                        )
                    }

                    // 连接状态切换按钮（右边）
                    Button(
                        onClick = { viewModel.toggleStreaming() },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
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
                            contentDescription = if (isStreaming) "断开" else "连接",
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            if (isStreaming) "Disconnect Video" else "Connect Video",
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Parameters") },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newX,
                            onValueChange = { newX = it },
                            label = { Text("X ") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newY,
                            onValueChange = { newY = it },
                            label = { Text("Y ") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newZ,
                            onValueChange = { newZ = it },
                            label = { Text("Z ") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newDistance,
                            onValueChange = { newDistance = it },
                            label = { Text("Distance") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newAngle,
                            onValueChange = { newAngle = it },
                            label = { Text("Angle") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateObjectCoordinates(
                        newX.toDouble(),
                        newY.toDouble(),
                        newZ.toDouble(),
                        newDistance.toDouble(),
                        newAngle.toInt()
                    )
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}
