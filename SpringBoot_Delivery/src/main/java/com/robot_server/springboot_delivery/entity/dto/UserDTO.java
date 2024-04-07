package com.robot_server.springboot_delivery.entity.dto;


import lombok.Data;

/**
 * 接收前端用户登录请求的数据
 */

@Data
public class UserDTO {
    private int id;
    private String username;
    private String password;
    private String token;
    private String faceurl;
    private String location;
    private String studentid;
}
