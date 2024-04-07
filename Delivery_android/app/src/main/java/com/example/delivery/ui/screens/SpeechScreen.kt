package com.example.delivery.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.UIVariables
import com.example.delivery.common.order.Receiver
import com.example.delivery.common.robot.DoorServerBind
import com.example.delivery.common.robot.SpeechServerBind
import com.example.delivery.navigation.Screen
import com.example.delivery.ui.components.TopBar
import com.example.delivery.utils.RobotUtils
import okhttp3.internal.notifyAll
import okhttp3.internal.wait

@Composable
fun SpeechScreen(navController: NavController) {

    val avatarText by remember {
        UIVariables.avatarText
    }

    val inputText by remember {
        UIVariables.inputText
    }

    val alertDialog = remember { mutableStateOf(UIVariables.showDialog) }

    Scaffold(
        topBar = { TopBar {
            Text(
                text = "语音指令" ,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        } }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = inputText,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "点击按钮并说出您要送到的位置，小洛会帮您查找用户哦，例如“帮我送到207”",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        if(SpeechServerBind.getInstance().getStub()?.isInitComplete!!) {
                            SpeechServerBind.getInstance().getStub()?.avatarClick()
                        }
                        else {
                            //正在升级
                            navController.navigate(Screen.OrderScreen.route)
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = avatarText)
                }

            }
        }
    }
    ShowDialog(alertDialog = alertDialog, navController = navController)
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowDialog(alertDialog: MutableState<Boolean>, navController: NavController) {
    val context = LocalContext.current

    //动态为checkbox获取check值
    val list = mutableListOf<Boolean>()
    GlobalVariables.users.forEach {
        list.add(false)
    }

    var checkStates by remember {
        mutableStateOf(list)
    }

    if(alertDialog.value) {
        AlertDialog(
            //按钮之外的关闭方式
            onDismissRequest = {
                UIVariables.showDialog = false
                alertDialog.value = UIVariables.showDialog
            },
            //标题
            title = {
                Text(text = "请选择一个对象")
            },

            text = {
                Column(
                    modifier = Modifier.verticalScroll(
                        state = rememberScrollState(),
                        enabled = true,
                        reverseScrolling = true
                    ),
                ) {
                    Row() {
                        Text(text = "位置", Modifier.padding(10.dp), textAlign = TextAlign.Center)
                        Text(text = "姓名", Modifier.padding(10.dp), textAlign = TextAlign.Center)
                        Text(text = "学号", Modifier.padding(10.dp), textAlign = TextAlign.Center)
                    }


                    checkStates.forEachIndexed { i, item ->

                        Row() {
                            Text(text = GlobalVariables.users[i].location, Modifier.padding(10.dp), textAlign = TextAlign.Center)
                            Text(text = GlobalVariables.users[i].name, Modifier.padding(10.dp), textAlign = TextAlign.Center)
                            Text(text = GlobalVariables.users[i].id, Modifier.padding(10.dp), textAlign = TextAlign.Center)

                            Checkbox(
                                checked = item,
                                onCheckedChange = {
                                    Log.i("OrderScreen", "$i + $item")
                                    checkStates = checkStates.mapIndexed { index, b ->
                                        if(i == index) {
                                            !b
                                        } else {
                                            //确保只能选择一个框
                                            if(b) {
                                                !b
                                            }
                                            else {
                                                b
                                            }
                                        }
                                    } as MutableList<Boolean>
                                },
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp),
                            )
                        }
                    }
                }
            },


            //确认按钮
            confirmButton = {
                TextButton(onClick = {
                    var tag = -1
                    repeat(checkStates.size) {
                        if(checkStates[it]) {
                            checkStates[it] = false
                            tag = it
                        }
                    }
                    if(tag == -1) {
                        Toast.makeText(context, "还没有选择用户", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        UIVariables.showDialog = false
                        alertDialog.value = UIVariables.showDialog
                        Receiver.name = GlobalVariables.users[tag].name
                        Receiver.id = GlobalVariables.users[tag].id
                        Receiver.dest = GlobalVariables.users[tag].location
                        Receiver.url = GlobalVariables.users[tag].faceurl
                        Receiver.sex = GlobalVariables.users[tag].sex
                        Toast.makeText(context, "已选择该用户", Toast.LENGTH_SHORT).show()

//                        GlobalVariables.QUERY_USER_SUCCESS = true
                        synchronized(Constants.QUERY_LOCK) {
                            Constants.QUERY_LOCK.notifyAll()
                        }
                        navController.popBackStack()
                        navController.popBackStack()
                        RobotUtils.voicePrompts("小洛准备出发了")
                        DoorServerBind.getInstance().getInterface().controlCargoDoor(2) //关舱
                    }
                }) {
                    Text(text = "确认选择")
                }
            },
            //取消按钮
            dismissButton = {
                TextButton(
                    onClick = {
                        repeat(checkStates.size) {
                            if(checkStates[it]) {
                                checkStates[it] = false
                            }
                        }
                        UIVariables.showDialog = false
                        alertDialog.value = UIVariables.showDialog
                    }
                ) {
                    Text(text = "重新选择")
                }
            },

            properties = DialogProperties(
                //是否通过后退按钮关闭对话框
                dismissOnBackPress = false,
                //是否通过点击对话框外部关闭对话框
                dismissOnClickOutside = false,
                securePolicy = SecureFlagPolicy.Inherit,
                usePlatformDefaultWidth = true
            )
        )
    }
}

@Preview
@Composable
fun SpeechScreenPreview() {
    val navController = rememberNavController()
    SpeechScreen(navController = navController)
}