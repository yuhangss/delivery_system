package com.robot_server.springboot_delivery.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.ExcelReader;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.Admin;
import com.robot_server.springboot_delivery.entity.FileData;
import com.robot_server.springboot_delivery.entity.Map;
import com.robot_server.springboot_delivery.entity.dto.AdminDTO;
import com.robot_server.springboot_delivery.entity.dto.UserDTO;
import com.robot_server.springboot_delivery.mapper.FileMapper;
import com.robot_server.springboot_delivery.mapper.UserMapper;
import com.robot_server.springboot_delivery.service.IMapService;
import com.robot_server.springboot_delivery.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.*;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.robot_server.springboot_delivery.service.IUserService;
import com.robot_server.springboot_delivery.entity.User;


import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author syh
 * @since 2023-01-08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private IMapService mapService;

    /**
     * 增删改，服务器调用
     * 人脸图像部分将图像写入磁盘，数据库中保存图片的url
     * @param user 用户信息
     * @return boolean
     */
    @PostMapping
    public boolean insert(@RequestBody User user){
        return userService.saveOrUpdate(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        User user = userService.getOne(queryWrapper);

        String url = user.getFaceurl();
        if(!url.equals("")) {
            //删除图像文件记录
            QueryWrapper<FileData> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("url", url);
            int num = fileMapper.delete(queryWrapper1);

            //删除图像文件磁盘存储
            String[] list = user.getFaceurl().split("/");
            String filename = list[list.length - 1];
            String filepath = fileUploadPath + filename;
            File file = new File(filepath);
            if(file.exists()) {
                file.delete();
            }
        }
        //删除用户记录
        return userService.removeById(id);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.list();
    }

    /**
     * 分页查询，将用户信息展示在服务器前端
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param username 用户名
     * @param studentid 学号
     * @return 分页结果
     */
    @GetMapping("/page") //分页查询 mybatis-plus
    public IPage<User> findPage(@RequestParam int pageNum,
                                @RequestParam int pageSize,
                                @RequestParam String username,
                                @RequestParam String studentid) {

        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        queryWrapper.like("studentid", studentid);
        queryWrapper.orderByDesc("id");
        Admin currentAdmin = TokenUtils.getCurrentAdmin();
        System.out.println("当前管理员=====================" + currentAdmin.getAdmin());
        return userService.page(page, queryWrapper);
    }

    /**
     * 批量删除接口
     * @param ids id集合
     * @return t 是否将所有id记录删除
     */
    @DeleteMapping ("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        boolean t = false;
        for (int id : ids) {
            t = delete(id);
        }
        return t;
    }

    /**
     * 用户信息导出接口，服务端调用
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        //从数据库查询出所有数据
        List<User> list = userService.list();

        ExcelWriter writer = ExcelUtil.getWriter(true);

        //对象写入excel，并输出标题
        writer.write(list, true);

        //设置浏览器相应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("导出信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }


    /**
     * 用户信息导入接口，服务端调用
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/import")
    public boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.readAll(User.class);
        return userService.saveBatch(list);
    }


     // ==============================以下接口提供实现用户端相应功能==============================


    /**
     * 用户登录接口
     * 安卓或小程序端调用该接口，进行身份验证
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误!");
        }
        UserDTO dto = userService.login(userDTO);
        return Result.success(dto);
    }

    /**
     * 用户信息查询接口
     * 通过前端传递用户名，返回该用户信息
     * @param username
     * @return
     */
    @RequestMapping("/username/{username}")
    public User findOne(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        return userService.getOne(queryWrapper);
    }

    //==============================以下接口提供实现机器人端相应功能==============================


    @PostMapping("/robot/location")
    public Result getLocation_uuid(@RequestParam String studentid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentid", studentid);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return Result.error(Constants.CODE_402, "没有该用户");
        }
        QueryWrapper<Map> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("loc_name", user.getLocation());
        Map map = mapService.getOne(queryWrapper1);
        if(map == null) {
            return Result.error(Constants.CODE_402, "地址为空");
        }
        return Result.success("操作成功", map.getLoc_uuid());
    }
}