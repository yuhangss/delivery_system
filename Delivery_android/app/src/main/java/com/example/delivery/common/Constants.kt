package com.example.delivery.common

import androidx.compose.runtime.mutableStateOf
import com.example.delivery.MainActivity
import com.example.delivery.common.Constants.BASEURL
import com.example.delivery.common.Constants.URL_INFO
import com.example.delivery.common.facenet.FaceFeatures
import com.example.delivery.common.facenet.FaceNet

/**
 * 存放全局变量
 * @property BASEURL 本机ip:port
 * @property BASEURL_ROBOT 机器人底盘ip:port
 *
 * 机器人GET型接口
 * @property URL_INFO 机器人信息获取接口
 *
 * 机器人POST型接口
 * @property URL_NAVIGATION_START 机器人导航到目的位置接口
 * @property URL_NAVIGATION_PAUSE 机器人暂停导航任务
 *
 * @property URL_MSG 机器人发送报文接口 POST
 * @property URL_GET_ORDER 机器人获取订单接口 POST
 * @property URL_UPDATE_ORDER 机器人更新订单接口 POST
 * @property URL_FINISH_ORDER 机器人完成订单接口 POST
 * @property URL_FUZZY_QUERY 机器人模糊查询用户接口 POST
 *
 * @property MODEL_NAME 人脸识别模型名称
 *
 * 设置机器人订单完成状态
 * @property STATE_FREE 机器人处于空闲状态
 * @property STATE_PICK 机器人处于取货状态(拿到物品)
 * @property STATE_DELIVERY 机器人处于送货状态(送出物品)
 * @property STATE_MOVE_TO_SRC 机器人向取货位置移动
 * @property STATE_MOVE_TO_DEST 机器人向送货地址移动
 */


object Constants {
    /**
     * @property BASEURL 本机ip:port
     */
    var BASEURL = mutableStateOf("http://192.168.43.49:9090")
    private const val BASEURL_ROBOT = "http://192.168.199.190:8088"

    /**
     * 机器人GET型接口
     * @property URL_INFO 机器人信息获取接口
     */
    const val URL_INFO = "$BASEURL_ROBOT/get_id"
    const val URL_NAVIGATION_STATE = "$BASEURL_ROBOT/navigation_get_status"

    /**
     * 机器人POST型接口
     * @property URL_NAVIGATION_START 机器人导航到目的位置接口
     * @property URL_NAVIGATION_PAUSE 机器人暂停导航任务
     */
    const val URL_TASK = "$BASEURL_ROBOT/task"

    /**
     * @property URL_MSG 机器人发送报文接口 POST
     * @property URL_GET_ORDER 机器人获取订单接口 POST
     * @property URL_UPDATE_ORDER 机器人更新订单接口 POST
     * @property URL_FINISH_ORDER 机器人完成订单接口 POST
     * @property URL_FUZZY_QUERY 机器人模糊查询用户接口 POST
     */
    var URL_MSG = "${BASEURL.value}/robot/msg"
    var URL_GET_ORDER = "${BASEURL.value}/delivery/robot"
    var URL_UPDATE_ORDER = "${BASEURL.value}/delivery/robot/update"
    var URL_FUZZY_QUERY = "${BASEURL.value}/delivery/robot/query"
    var URL_QUERY_LOCATION = "${BASEURL.value}/map/get/loc_uuid"

    var URL_UPDATE_STATE = "${BASEURL.value}/delivery/robot/state"

    /**
     * @property MODEL_NAME 人脸识别模型名称
     */
    const val MODEL_NAME = "FaceNet.pb"


    /**
     * 设置机器人订单完成状态
     * @property STATE_FREE 机器人处于空闲状态
     * @property STATE_PICK 机器人处于取货状态(拿到物品)
     * @property STATE_DELIVERY 机器人处于送货状态(送出物品)
     * @property STATE_MOVE_TO_SRC 机器人向取货位置移动
     * @property STATE_MOVE_TO_DEST 机器人向送货地址移动
     */
    const val STATE_FREE = "free"
    const val STATE_MOVE_TO_SRC = "move to src"
    const val STATE_PICK = "picking up"
    const val STATE_MOVE_TO_DEST = "move to dest"
    const val STATE_DELIVERY = "delivery"

    /**
     * MQTT主题订阅
     */
    const val SUB_TOPIC = "robint/uplink"
    const val HOST = "tcp://192.168.199.190:1883"

    lateinit var faceNet: FaceNet

    /**
     * 锁
     */
    val FINISH_LOCK = Any()
    val FACE_LOCK = Any()
    val QUERY_LOCK = Any()
}