package com.robot_server.springboot_delivery.entity;

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
  @ApiModel(value = "Robot对象", description = "")
public class Robot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "robot_uuid", type = IdType.AUTO)
    private String robot_uuid;

    private String name;

    private String ip;

    private String group_uuid;

    private String state;

    private String mac;

    private LocalDateTime create_time;

}
