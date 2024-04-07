package com.robot_server.springboot_delivery.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.ExcelReader;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.Delivery;
import com.robot_server.springboot_delivery.mapper.DeliveryMapper;
import com.robot_server.springboot_delivery.utils.CodeUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

import com.robot_server.springboot_delivery.service.IRobotService;
import com.robot_server.springboot_delivery.entity.Robot;


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
@RequestMapping("/robot")
public class RobotController {

    @Resource
    private IRobotService robotService;

    @Resource
    private DeliveryMapper deliveryMapper;

    /**
     * 增删改，服务器调用
     * @param robot
     * @return
     */

    @PostMapping
    public boolean insert(@RequestBody Robot robot) {
        return robotService.saveOrUpdate(robot);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) {
        return robotService.removeById(id);
    }

    @GetMapping
    public List<Robot> findAll() {
        return robotService.list();
    }

    /**
     * 分页查询，将机器人信息展示在服务器前端
     * @param pageNum
     * @param pageSize
     * @param name 底盘唯一序号
     * @param robot_uuid 机器人uuid
     * @return
     */
    @GetMapping("/page") //分页查询 mybatis-plus
    public IPage<Robot> findPage(@RequestParam int pageNum,
                                @RequestParam int pageSize,
                                 @RequestParam String name,
                                 @RequestParam String robot_uuid) {

        IPage<Robot> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Robot> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        queryWrapper.like("robot_uuid", robot_uuid);
        return robotService.page(page, queryWrapper);
    }

    /**
     * 批量删除接口
     * @param ids
     * @return
     */
    @DeleteMapping ("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return robotService.removeByIds(ids);
    }

    /**
     * 机器人信息导出接口，服务端调用
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        //从数据库查询出所有数据
        List<Robot> list = robotService.list();

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
     * 机器人信息导入接口，服务端调用
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/import")
    public boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Robot> list = reader.readAll(Robot.class);
        return robotService.saveBatch(list);
    }


     // ===============================以下接口实现机器人安卓与服务器通信的相关功能===============================


    /**
     * 报文接收接口，用来接收机器人向后端发送的状态报文
     * 以机器人唯一标识id更新对应数据
     * @param robot 将报文以json的格式传来
     */
    @PostMapping("/msg")
    public Result getMsg(@RequestBody Robot robot) {
        return Result.success(robotService.update().eq("robot_uuid", robot.getRobot_uuid()).update(robot));
    }
}

