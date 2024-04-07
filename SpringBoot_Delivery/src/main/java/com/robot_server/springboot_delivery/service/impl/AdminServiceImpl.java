package com.robot_server.springboot_delivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.entity.Admin;
import com.robot_server.springboot_delivery.entity.dto.AdminDTO;
import com.robot_server.springboot_delivery.exception.ServiceException;
import com.robot_server.springboot_delivery.mapper.AdminMapper;
import com.robot_server.springboot_delivery.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot_server.springboot_delivery.utils.TokenUtils;
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
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Override
    public AdminDTO login(AdminDTO adminDTO) { //校验用户名密码
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin", adminDTO.getAdmin());
        queryWrapper.eq("password", adminDTO.getPassword());
        Admin one;
        try {
            one = getOne(queryWrapper);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if(one != null) {
            BeanUtil.copyProperties(one, adminDTO, true);
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword()); //生成token
            adminDTO.setToken(token);
            return adminDTO;
        }
        else {
            throw new ServiceException(Constants.CODE_500, "用户名或密码错误!");
        }
    }
}
