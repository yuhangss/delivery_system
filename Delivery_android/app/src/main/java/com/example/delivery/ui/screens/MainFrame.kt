package com.example.delivery.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.delivery.CameraX
import com.example.delivery.MainActivity
import com.example.delivery.common.GlobalVariables
import com.example.delivery.model.entity.NavigationItem
import com.example.delivery.navigation.Screen
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.material.bottomsheet.BottomSheetDialog

@Composable
fun MainFrame(navController: NavController) {

    val navigationItems = listOf(
        NavigationItem(title = "主页", icon = Icons.Filled.Home),
        NavigationItem(title = "日志", icon = Icons.Filled.Menu),
        NavigationItem(title = "关于", icon = Icons.Filled.Info)
    )

    var currentNavigationIndex by remember {
        mutableStateOf(0)
    }


    ProvideWindowInsets {

        Scaffold(bottomBar = {

            BottomNavigation(
                backgroundColor = MaterialTheme.colors.surface,
//            modifier = Modifier.navigationBarsPadding()
            ) {
                navigationItems.forEachIndexed { index, navigationItem ->
                    BottomNavigationItem(
                        selected = currentNavigationIndex == index,
                        onClick = {
                            currentNavigationIndex = index
                        },
                        icon = {
                            Icon(imageVector = navigationItem.icon, contentDescription = null)
                        },
                        label = {
                            Text(text = navigationItem.title)
                        },
                        selectedContentColor = Color(0xFFFF8400),
                        unselectedContentColor = Color(0xFF999999))
                }
            }
        }) {
            when(currentNavigationIndex) {
                0 -> HomeScreen(navController)
                1 -> LogcatView()
                2 -> AboutScreen()
            }
        }
    }

}

