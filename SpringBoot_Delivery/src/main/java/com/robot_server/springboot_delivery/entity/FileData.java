package com.robot_server.springboot_delivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("file")
public class FileData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String filename;
    private String type;
    private Long size;
    private String url;
    private boolean is_delete;
    private Boolean enable;
    private String md5;
}
