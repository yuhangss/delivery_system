package com.robot_server.springboot_delivery.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.entity.Admin;
import com.robot_server.springboot_delivery.exception.ServiceException;
import com.robot_server.springboot_delivery.service.IAdminService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private IAdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(!(handler instanceof HandlerMethod)) { //如果不是映射到方法直接通过
            return true;
        }
        //执行认证
        if(StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.CODE_401, "无token,请重新登录");
        }
        //获取token中的userid
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException(Constants.CODE_401, "token验证失败,请重新登录");
        }

        Admin admin = adminService.getById(userId);
        if(admin == null) {
            throw new ServiceException(Constants.CODE_401, "用户不存在,请重新登录");
        }

        //用户密码加签验证token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(admin.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException j) {
            throw new ServiceException(Constants.CODE_401, "token验证失败,请重新登录");
        }

        return true;
    }
}
