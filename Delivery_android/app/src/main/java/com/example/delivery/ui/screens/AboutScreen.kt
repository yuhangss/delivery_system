package com.example.delivery.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.delivery.common.Constants
import com.example.delivery.common.HttpCallBack
import com.example.delivery.common.order.Robot
import com.example.delivery.common.robot.DoorServerBind
import com.example.delivery.common.robot.SpeechServerBind
import com.example.delivery.ui.components.TopBar
import com.example.delivery.utils.OkhttpUtils
import com.example.delivery.utils.OrderUtils
import com.example.delivery.utils.RobotUtils
import com.example.delivery.utils.mqtt.MqttUtils
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * 应用详情界面
 * 需要展示：
 * 送货教程、机器人信息？、版本信息、开发者、项目链接？
 */
@Composable
fun AboutScreen() {

    var text by remember {
        mutableStateOf("暂停任务")
    }

    var url by remember {
        Constants.BASEURL
    }

    val context = LocalContext.current
    Scaffold(
        topBar = { TopBar {
            Text(
                text = "关于" ,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        } }
    ) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.verticalScroll(
                    state = rememberScrollState(),
                    enabled = true,
                    reverseScrolling = true),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp),
                    onClick = {
                        text = if (text == "暂停任务") {
                            object : Thread() {
                                override fun run() {
                                    val hashMap: HashMap<String, String> = HashMap()
                                    hashMap["json"] = "{\"command_name\": \"navigation_pause\"}"
                                    OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
                                        override fun onError(errorLog: String) {
                                            Log.i("DeliveryOrder", errorLog + "暂停失败")

                                        }

                                        override fun onSuccess(message: String) {
                                            Log.i("DeliveryOrder", message + "暂停成功")
                                        }
                                    })
                                }
                            }.start()
                            "开始任务"
                        } else {
                            object : Thread() {
                                override fun run() {
                                    val hashMap: HashMap<String, String> = HashMap()
                                    hashMap["json"] = "{\"command_name\": \"navigation_resume\"}"
                                    OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
                                        override fun onError(errorLog: String) {
                                            Log.i("DeliveryOrder", errorLog + "开始失败")

                                        }

                                        override fun onSuccess(message: String) {
                                            Log.i("DeliveryOrder", message + "开始成功")

                                        }
                                    })
                                }
                            }.start()
                            "暂停任务"
                        }
                    },
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = {
                        object : Thread() {
                            override fun run() {
                                RobotUtils.moveToCharge()
                            }
                        }.start()
                    }
                ) {
                    Text(text = "取消任务并返回充电桩")
                }

                Button(
                    onClick = {
                        object : Thread() {
                            override fun run() {
                                val hashMap: HashMap<String, String> = HashMap()
                                hashMap["json"] = "{\"command_name\": \"navigation_start\", \"location_uuid\": \"a7d18cdba6014c9ba94ddb7e7defa178\"}"
                                OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object :
                                    HttpCallBack {
                                    override fun onError(errorLog: String) {
                                        Log.i("DeliveryOrder", "机器人移动请求错误-取货")
                                    }

                                    override fun onSuccess(message: String) {
                                        if(OrderUtils.isMoving(message)) {
                                            Log.i("DeliveryOrder", "机器人移动请求成功，正在向收货地址移动")
                                            Robot.is_moving = true //修改机器人移动状态
                                        }
                                        else {
                                            Log.i("DeliveryOrder", message + "机器人任务被占用")
                                        }
                                    }
                                })
                            }
                        }.start()
                    }
                ) {
                    Text(text = "移动")
                }

                Button(
                    onClick = {
                        DoorServerBind.getInstance().getInterface().controlCargoDoor(1)
                    }
                ) {
                    Text(text = "开舱")
                }

                Button(
                    onClick = {
                        DoorServerBind.getInstance().getInterface().controlCargoDoor(2)
                    }
                ) {
                    Text(text = "关舱")
                }

                TextField(
                    value = url,
                    onValueChange = {
                        url = it
                    }
                )
                Button(
                    onClick = {
                        Log.i("url", url)
                        Constants.URL_MSG = "${url}/robot/msg"
                        Constants.URL_GET_ORDER = "${url}/delivery/robot"
                        Constants.URL_UPDATE_ORDER = "${url}/delivery/robot/update"
                        Constants.URL_FUZZY_QUERY = "${url}/delivery/robot/query"
                        Constants.URL_QUERY_LOCATION = "${url}/map/location"
                        Constants.URL_UPDATE_STATE = "${url}/delivery/robot/state"
                    }) {
                    Text(text = "更改url")
                }
            }
        }

    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}