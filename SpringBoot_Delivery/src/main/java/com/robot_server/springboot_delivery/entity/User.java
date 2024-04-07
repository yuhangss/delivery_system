package com.robot_server.springboot_delivery.entity;

import cn.hutool.bloomfilter.bitMap.BitMap;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author syh
 * @since 2023-01-08
 */
@Getter
@Setter
  @ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名")
    private String username;

    private String studentid;

    @ApiModelProperty("电话，可无")
    private String phone;

    @ApiModelProperty("密码（）")
    private String password;

    @ApiModelProperty("创建时间")
    private LocalDateTime create_time;

    @ApiModelProperty("人脸信息")
    private String faceurl;

    @ApiModelProperty("地点")
    private String location;

    @ApiModelProperty("性别")
    private String sex;

}
