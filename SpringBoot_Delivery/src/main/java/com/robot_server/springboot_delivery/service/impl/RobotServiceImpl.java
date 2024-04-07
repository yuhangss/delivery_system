package com.robot_server.springboot_delivery.service.impl;

import com.robot_server.springboot_delivery.entity.Robot;
import com.robot_server.springboot_delivery.mapper.RobotMapper;
import com.robot_server.springboot_delivery.service.IRobotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syh
 * @since 2023-01-08
 */
@Service
public class RobotServiceImpl extends ServiceImpl<RobotMapper, Robot> implements IRobotService {

}
