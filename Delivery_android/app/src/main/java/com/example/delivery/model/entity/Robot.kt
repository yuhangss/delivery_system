package com.example.delivery.model.entity

/**
 * 保存机器人的基础信息,(数据库)字段的一部分
 * 仅在开机时(?)时自动获取一次
 * @property name 底盘唯一序号，方便人眼识别
 * @property robot_uuid 底盘uuid，根据name生成
 * @property ip ip
 * @property mac mac
 * @property group 分组
 */
class Robot constructor(name_: String, robot_uuid_: String, ip_: String, mac_: String, group_: String) {
    private var name: String = ""
    private var robot_uuid: String = ""
    private var ip: String = ""
    private var mac: String = ""
    private var group: String = ""

    init {
        name = name_
        robot_uuid = robot_uuid_
        ip = ip_
        mac = mac_
        group = group_
    }
}