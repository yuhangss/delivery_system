package com.robot_server.springboot_delivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.entity.Admin;
import com.robot_server.springboot_delivery.entity.User;
import com.robot_server.springboot_delivery.entity.dto.UserDTO;
import com.robot_server.springboot_delivery.exception.ServiceException;
import com.robot_server.springboot_delivery.mapper.UserMapper;
import com.robot_server.springboot_delivery.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    public UserDTO login(UserDTO userDTO) { //校验用户名密码
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if(one != null) {
            BeanUtil.copyProperties(one, userDTO, true);
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword()); //生成token
            userDTO.setToken(token);
            return userDTO;
        }
        else {
            throw new ServiceException(Constants.CODE_500, "用户名或密码错误!");
        }
    }
}
