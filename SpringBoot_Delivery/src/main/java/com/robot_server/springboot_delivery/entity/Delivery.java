package com.robot_server.springboot_delivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单参数
 */

@Data
@TableName("delivery")
public class Delivery {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String sender;
    private String senderid;
    private String senderurl;
    private String receiverurl;
    private String src;
    private String dest;
    private String receiver;
    private String receiverid;
    private LocalDateTime create_time;
    private String robot_name;
    private String state;

    private String src_uuid;
    private String dest_uuid;
}
