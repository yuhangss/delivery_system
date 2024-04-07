package com.example.delivery.ui.screens

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.order.OrderState
import com.example.delivery.common.order.Receiver
import com.example.delivery.common.order.Robot
import com.example.delivery.common.order.Sender
import com.example.delivery.common.robot.DoorServerBind
import com.example.delivery.navigation.Screen
import com.example.delivery.utils.RobotUtils
import kotlinx.coroutines.*
import okhttp3.internal.notifyAll
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

@Composable
fun CountdownScreen(navController: NavController) {
    val remainingTime = remember { mutableStateOf(60) }
    val handler = Handler(Looper.getMainLooper()) { p0 ->
        if (p0.what == 0x200) {
            finishOrder(navController)
        }
        true
    }

    DisposableEffect(Unit) {
        val timer = Timer()
        timer.scheduleAtFixedRate(0L, 1000L) {
            remainingTime.value--
            if (remainingTime.value == 0) {
                timer.cancel()
                handler.sendEmptyMessage(0x200)
            }
        }

        onDispose {
            timer.cancel()
            // 执行清理逻辑，如退出该界面
        }
    }

    // 渲染界面，显示剩余时间
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "${remainingTime.value.toString()}s",
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "请尽快取走您的物品并点击\"完成订单\"按钮结束订单\n" +
                        "点击\"重置计时\"按钮可以重置舱门关闭计时",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    onClick = {
                        finishOrder(navController)
                    },
                    modifier = Modifier
                        .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                        .padding(16.dp), // 添加一些内边距
                    shape = RoundedCornerShape(30.dp) // 设置圆角矩形的形状
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Done icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "完成订单",
                        fontSize = 20.sp
                    )
                }

                Button(
                    onClick = {
                        remainingTime.value = 60
                    },
                    modifier = Modifier
                        .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                        .padding(16.dp), // 添加一些内边距
                    shape = RoundedCornerShape(30.dp) // 设置圆角矩形的形状
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "重置计时",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

fun finishOrder(navController: NavController) {
    navController.popBackStack()
    navController.popBackStack()

    Sender.initSender()
    Receiver.initReceiver()
    OrderState.initOrderState()
    Robot.initRobot()
    GlobalVariables.STATE_ORDER = Constants.STATE_FREE //订单完成，将机器人重新置为空闲状态
//    GlobalVariables.FACE_RECOGNITION_SUCCESS = false //人脸识别标志置0，准备接收下一次订单
    GlobalVariables.OPEN_CAMERA_SUCCESS.value = false

    DoorServerBind.getInstance().getInterface().controlCargoDoor(2)
    RobotUtils.moveToCharge()

    synchronized(Constants.FINISH_LOCK) { //获取锁
        Constants.FINISH_LOCK.notifyAll() //释放锁
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
fun FinishScreenPreView() {
    val navController = rememberNavController()
    CountdownScreen(navController = navController)
}