package com.example.delivery.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.delivery.CameraX
import com.example.delivery.ui.screens.MainFrame

@Composable
fun Router(context: Context) {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = Screen.MainFrame.route) {
        composable(route = Screen.MainFrame.route) {
            MainFrame(navController = navController)
        }
        composable(
            route = Screen.CameraX.route,
        ) {
            CameraX(navController = navController)
        }
    }
}