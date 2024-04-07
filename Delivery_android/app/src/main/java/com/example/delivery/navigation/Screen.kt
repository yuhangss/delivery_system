package com.example.delivery.navigation

sealed class Screen(val route: String) {
    object OrderScreen: Screen("order_screen")
    object MainFrame: Screen("main_frame")
    object CameraX: Screen("camera_x")
    object SpeechScreen: Screen("speech_screen")
    object FinishScreen: Screen("finish_screen")
}
