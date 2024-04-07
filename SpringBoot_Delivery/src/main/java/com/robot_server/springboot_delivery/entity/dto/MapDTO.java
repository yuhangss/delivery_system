package com.robot_server.springboot_delivery.entity.dto;

/**
 * 返回相关地址信息，提供给机器人导航
 */

import lombok.Data;

@Data
public class MapDTO {
    private String map_uuid;
    private String loc_uuid;
    private String loc_name;
    private String group_uuid;
}
