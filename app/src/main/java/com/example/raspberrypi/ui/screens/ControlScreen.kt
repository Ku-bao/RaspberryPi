package com.example.raspberrypi.ui.screens

import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.raspberrypi.data.api.NetworkClient
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
    val isDetect by viewModel.isDetect.collectAsState()
    val angle by viewModel.angle.collectAsState()
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
                title = { Text("Remote Control Mode") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.closeVideo()
                        navController.navigateUp()}) {
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
                    .fillMaxHeight()
                    .weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val leftButtonInteractionSource = remember { MutableInteractionSource() }
                    val isLeftButtonPressed by leftButtonInteractionSource.collectIsPressedAsState()
                    
                    LaunchedEffect(isLeftButtonPressed) {
                        if (isLeftButtonPressed) {
                            viewModel.turnLeft()
                        } else {
                            viewModel.stopTurnLeft()
                        }
                    }
                    
                    Button(
                        onClick = { },
                        interactionSource = leftButtonInteractionSource,
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Left")
                    }
                    
                    val rightButtonInteractionSource = remember { MutableInteractionSource() }
                    val isRightButtonPressed by rightButtonInteractionSource.collectIsPressedAsState()
                    
                    LaunchedEffect(isRightButtonPressed) {
                        if (isRightButtonPressed) {
                            viewModel.turnRight()
                        } else {
                            viewModel.stopTurnRight()
                        }
                    }
                    
                    Button(
                        onClick = { },
                        interactionSource = rightButtonInteractionSource,
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Right")
                    }
                    // 摇杆组件
                    Joystick(
                        onDirectionChange = { x, y ->
                            viewModel.sendControlData(x, y)
                        }
                    )
                }
            }

            // 第二部分：视频流区域
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .aspectRatio(1f) // 保持1:1比例
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isConnected and isStreaming) {
                    var videoUrl = "http://${NetworkClient.getIpAddress()}:${NetworkClient.getPort()}/video"
                    if (isDetect) {
                        videoUrl = "http://${NetworkClient.getIpAddress()}:${NetworkClient.getPort()}/detection_video"
                    }
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Offset angle:",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 2.dp)
                            )
                            Text(
                                text = "${angle.toInt()}°",
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
                            Text("Mode\n1",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                        }
                        Button(
                            onClick = { viewModel.setModel2() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Mode\n2",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                        }
                        Button(
                            onClick = { viewModel.setModel3() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Mode\n3",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                        }
                    }

                    // 开始抓取按钮
                    Button(
                        onClick = { viewModel.Grasp() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Grab")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 开启检测按钮
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
                                // modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = if (isDetect) "Stop\nDetect" else "Start\nDetect",
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
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
                                //modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = if (isStreaming) "Disconnect\nVideo" else "Connect\nVideo",
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
