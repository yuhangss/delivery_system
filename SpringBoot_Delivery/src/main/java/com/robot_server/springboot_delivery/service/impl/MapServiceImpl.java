package com.robot_server.springboot_delivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.entity.Map;
import com.robot_server.springboot_delivery.entity.dto.MapDTO;
import com.robot_server.springboot_delivery.exception.ServiceException;
import com.robot_server.springboot_delivery.mapper.MapMapper;
import com.robot_server.springboot_delivery.service.IMapService;
import org.springframework.stereotype.Service;

/**
 * 服务类实现
 * 对地址进行校验，判断是否有loc_name对应的loc_uuid
 */

@Service
public class MapServiceImpl extends ServiceImpl<MapMapper, Map> implements IMapService {
    @Override
    public Map checkLoc(String loc_name) {
        QueryWrapper<Map> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loc_name", loc_name);
        Map map;
        try {
            map = getOne(queryWrapper);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if(map != null) {
            return map;
        }
        else {
            throw new ServiceException(Constants.CODE_700, "地址错误，请检查所给地址信息是否正确");
        }
    }
}
