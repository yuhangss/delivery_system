package com.robot_server.springboot_delivery.service;

import com.robot_server.springboot_delivery.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robot_server.springboot_delivery.entity.dto.AdminDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syh
 * @since 2023-01-08
 */
public interface IAdminService extends IService<Admin> {

    AdminDTO login(AdminDTO adminDTO);
}
