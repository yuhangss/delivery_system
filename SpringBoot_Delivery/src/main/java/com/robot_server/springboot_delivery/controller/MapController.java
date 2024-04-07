package com.robot_server.springboot_delivery.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.Map;
import com.robot_server.springboot_delivery.service.IMapService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/map")
public class MapController {
    @Resource
    private IMapService mapService;

    @PostMapping("/get/loc_uuid")
    public Result getLocation_uuid(@RequestParam String loc_name) {
        QueryWrapper<Map> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loc_name", loc_name);
        Map map = mapService.getOne(queryWrapper);

        if (map == null) {
            return Result.error(Constants.CODE_402, "地址空数据异常");
        }
        return Result.success("获取地址id成功", map.getLoc_uuid());
    }
}
