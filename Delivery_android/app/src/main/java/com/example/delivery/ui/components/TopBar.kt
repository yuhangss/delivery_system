package com.example.delivery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController


/**
 * 统一标题栏
 * @param content 标题内容
 */
@Composable
fun TopBar(content: @Composable () -> Unit) {

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = Unit) {
        systemUiController.setSystemBarsColor(Color.Transparent)
    }

    //默认标题栏高度
    val appBarHeight = 50.dp

    //转换状态栏高度为dp,像素(px)转化为dp
    var statusBarHeightDp = with(LocalDensity.current) {
        LocalWindowInsets.current.statusBars.top.toDp()
    }

    statusBarHeightDp = 30.dp

    Row(
        modifier = Modifier
//            .background(
//                Brush.linearGradient(
//                    listOf(MaterialTheme.colors.primary, MaterialTheme.colors.secondary)
//                )
//            )
            .fillMaxWidth()
            .height(appBarHeight + statusBarHeightDp)
            .padding(top = statusBarHeightDp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar() {
        Text(text = "标题")
    }
}