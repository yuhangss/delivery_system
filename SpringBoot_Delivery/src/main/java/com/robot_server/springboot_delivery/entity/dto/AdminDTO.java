package com.robot_server.springboot_delivery.entity.dto;

import lombok.Data;

/**
 * 接收前端管理员登录请求的数据
 */

@Data
public class AdminDTO {
    private int id;
    private String admin;
    private String password;
    private String token;
}
