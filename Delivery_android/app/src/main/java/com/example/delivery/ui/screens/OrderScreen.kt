package com.example.delivery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.UIVariables
import com.example.delivery.navigation.Screen
import com.example.delivery.ui.components.TopBar
import com.example.delivery.utils.RobotUtils

/**
 * 显示当前订单详情
 */
@Composable
fun OrderScreen(navController: NavController) {

    var text by remember {
        mutableStateOf("暂停任务")
    }

    val alertDialog = remember { mutableStateOf(UIVariables.showDialog) }

    val enabled_camera by remember {
        GlobalVariables.OPEN_CAMERA_SUCCESS
    }

    val enabled_speech by remember {
        GlobalVariables.ENABLE_SPEECH
    }

    val enabled_finish by remember {
        GlobalVariables.ENABLE_FINISH
    }

    Scaffold(
        topBar = { TopBar {
            Text(
                text = "订单派送" ,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        } }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.padding(25.dp)
                ) {
                    Text(
                        text = "机器人状态: ${
                            when (GlobalVariables.STATE_ORDER) {
                                Constants.STATE_FREE -> {
                                    "空闲"
                                }
                                Constants.STATE_MOVE_TO_SRC -> {
                                    "正在前往取货位置"
                                }
                                Constants.STATE_PICK -> {
                                    "正在取货"
                                }
                                Constants.STATE_MOVE_TO_DEST -> {
                                    "正在前往送货位置"
                                }
                                Constants.STATE_DELIVERY -> {
                                    "正在送货"
                                }
                                else -> {
                                    "状态错误"
                                }
                            }
                        }",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "请进行人脸识别",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        modifier = Modifier
                            .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                            .padding(16.dp), // 添加一些内边距
                        shape = RoundedCornerShape(30.dp), // 设置圆角矩形的形状
                        onClick = {
                            text = if (text == "暂停任务") {
                                RobotUtils.movePause()
                                "开始任务"
                            } else {
                                RobotUtils.moveResume()
                                "暂停任务"
                            }
                        },
                    ) {
                        Icon(
                            imageVector =
                            when (text) {
                                "暂停任务" -> {
                                    Icons.Default.Pause
                                }
                                "开始任务" -> {
                                    Icons.Default.PlayArrow
                                }
                                else -> {
                                    Icons.Default.Error
                                }
                            },
                            contentDescription =
                            when (text) {
                                "暂停任务" -> {
                                    "Pause icon"
                                }
                                "开始任务" -> {
                                    "Play icon"
                                }
                                else -> {
                                    "Error icon"
                                }
                            },
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = text,
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = {
                            //先将旋转放在这里，之后修改在语音指令之后
                            RobotUtils.startRotate("180") //旋转180，调用后置摄像头
                            navController.navigate(Screen.CameraX.route)
                        },
                        enabled = enabled_camera,
                        modifier = Modifier
                            .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                            .padding(16.dp), // 添加一些内边距
                        shape = RoundedCornerShape(30.dp) // 设置圆角矩形的形状
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Face icon",
                            tint = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "人脸识别",
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = {
                            navController.navigate(Screen.SpeechScreen.route)
                        },
                        modifier = Modifier
                            .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                            .padding(16.dp), // 添加一些内边距
                        shape = RoundedCornerShape(30.dp) ,// 设置圆角矩形的形状
                        enabled = enabled_speech
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Speech recognition icon",
                            tint = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "语音界面",
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = {
                            navController.navigate(Screen.FinishScreen.route)
                        },
                        modifier = Modifier
                            .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                            .padding(16.dp), // 添加一些内边距
                        shape = RoundedCornerShape(30.dp), // 设置圆角矩形的形状
                        enabled = enabled_finish
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer icon",
                            tint = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "订单计时",
                            fontSize = 20.sp
                        )
                    }

                }
            }
        }
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
fun OrderScreenPreView() {
    val navController = rememberNavController()
    OrderScreen(navController = navController)
}