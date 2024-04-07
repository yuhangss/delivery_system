package com.robot_server.springboot_delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robot_server.springboot_delivery.entity.User;
import com.robot_server.springboot_delivery.entity.dto.UserDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syh
 * @since 2023-01-08
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);
}
