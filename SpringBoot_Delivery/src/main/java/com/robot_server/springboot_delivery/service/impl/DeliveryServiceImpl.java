package com.robot_server.springboot_delivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot_server.springboot_delivery.entity.Delivery;
import com.robot_server.springboot_delivery.mapper.DeliveryMapper;
import com.robot_server.springboot_delivery.service.IDeliveryService;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements IDeliveryService {
}
