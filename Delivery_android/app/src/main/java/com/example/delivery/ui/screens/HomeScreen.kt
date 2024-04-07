package com.example.delivery.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.delivery.common.UIVariables
import com.example.delivery.navigation.Screen
import com.example.delivery.ui.components.TopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * 主页
 * 需要展示：
 * ？
 */
@Composable
fun HomeScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopBar {
                Text(
                    text = "主页",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        },) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.OrderScreen.route)
                    },
                    modifier = Modifier
                        .size(width = 200.dp, height = 150.dp) // 设置按钮的大小
                        .padding(16.dp), // 添加一些内边距
                    shape = RoundedCornerShape(30.dp) // 设置圆角矩形的形状
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Task icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "当前任务",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}