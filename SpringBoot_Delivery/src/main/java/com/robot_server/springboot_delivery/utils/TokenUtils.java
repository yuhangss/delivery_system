package com.robot_server.springboot_delivery.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.robot_server.springboot_delivery.entity.Admin;
import com.robot_server.springboot_delivery.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class TokenUtils {

    private static IAdminService staticAdminService;

    @Resource
    private IAdminService adminService;

    /**
     * 将adminService注册为bean
     * 再赋给静态对象
     */
    @PostConstruct
    public void setAdminService() {
        staticAdminService = adminService;
    }

    /**
     * 生成token
     * @return
     */
    public static String genToken(String userId, String sign) {
        return JWT.create().withAudience(userId) //将userId保存到token里，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) //2小时后token过期
                .sign(Algorithm.HMAC256(sign)); //以密码作为密钥
    }

    /**
     * 获取token中的用户信息
     * @return
     */
    public static Admin getCurrentAdmin() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if(!StrUtil.isBlank(token)) {
                String aud = JWT.decode(token).getAudience().get(0);
                Integer adminId = Integer.valueOf(aud);
                return staticAdminService.getById(adminId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
