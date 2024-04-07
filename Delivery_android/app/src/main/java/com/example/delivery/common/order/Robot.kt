package com.example.delivery.common.order

import com.example.delivery.common.GlobalVariables


/**
 * @property is_arrived_src 机器人是否到达收货地址
 * @property is_arrived_dest 机器人是否到达送货位置
 * @property robot_name 机器人name
 * @property src_uuid 收货位置uuid
 * @property dest_uuid 送货位置uuid
 * @property is_moving 机器人是在移动
 */
object Robot {
    var robot_name = ""
    var src_uuid = ""
    var dest_uuid = ""
    var is_moving = true
    var state = "Ready"
    var reached = false

    fun initRobot() {
        this.robot_name = GlobalVariables.name
        this.src_uuid = ""
        this.dest_uuid = ""
        this.is_moving = true
        this.state = "Ready"
        this.reached = false
    }
}