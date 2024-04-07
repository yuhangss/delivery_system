package com.example.delivery.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import com.example.delivery.MainActivity
import com.example.delivery.common.bean.UserDTO

/**
 * @property STATE_ORDER 机器人当前状态，初始默认空闲
 * @property OPEN_CAMERA 摄像头打开标志
 * @property FACE_RECOGNITION_SUCCESS 人脸识别成功标志
 */
object GlobalVariables {
    var STATE_ORDER = Constants.STATE_FREE
    var OPEN_CAMERA_SUCCESS = mutableStateOf(false)
    var ENABLE_SPEECH = mutableStateOf(false)
    var ENABLE_FINISH = mutableStateOf(false)

    var LOCATION = ""

    /**
     * 机器人状态信息，在程序开始时获取
     */
    var name = ""
    var ip = ""
    var mac = ""
    var robot_uuid = ""
    var group = ""


    lateinit var bitmap: Bitmap
    var isMoving = false

    var users = mutableListOf<UserDTO>()

    fun getRobotMsg(): String {
        return "{\n" +
                "\t\"name\": \"$name\",\n" +
                "\t\"ip\": \"$ip\",\n" +
                "\t\"state\": \"$STATE_ORDER\",\n" +
                "\t\"mac\": \"$mac\",\n" +
                "\t\"robot_uuid\": \"$robot_uuid\",\n" +
                "\t\"group_uuid\": \"$group\"\n" +
                "}"
    }
}