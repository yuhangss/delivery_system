package com.robot_server.springboot_delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robot_server.springboot_delivery.entity.Map;

public interface IMapService extends IService<Map> {
    Map checkLoc(String loc_name);
}
