package com.robot_server.springboot_delivery.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("map")
public class Map {
    @TableId(value = "loc_uuid", type = IdType.AUTO)
    private String loc_uuid;

    private String loc_name;
    private String map_uuid;
    private String group_uuid;

    private String x;
    private String y;
    private String qx;
    private String qy;
    private String qz;
    private String qw;
    private String ix;
    private String iy;
    private String itheta;
}
