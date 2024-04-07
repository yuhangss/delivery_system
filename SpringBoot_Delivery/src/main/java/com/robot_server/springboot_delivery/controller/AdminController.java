package com.robot_server.springboot_delivery.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.ExcelReader;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.dto.AdminDTO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.robot_server.springboot_delivery.service.IAdminService;
import com.robot_server.springboot_delivery.entity.Admin;


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
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private IAdminService adminService;

    /**
     * 管理员登录接口
     * 服务器端调用该接口
     * @param adminDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody AdminDTO adminDTO) {
        String admin = adminDTO.getAdmin();
        String password = adminDTO.getPassword();
        if(StrUtil.isBlank(admin) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误!");
        }
        AdminDTO dto = adminService.login(adminDTO);
        return Result.success(dto);
    }

    @RequestMapping("/{admin}")
    public Admin findOne(@PathVariable String admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("admin", admin);
        return adminService.getOne(queryWrapper);
    }

    @PostMapping
    public Result insert(@RequestBody Admin admin) {
        return Result.success(adminService.saveOrUpdate(admin));
    }
//
//    @DeleteMapping("/{id}")
//    public Result delete(@PathVariable int id) {
//        return Result.success(adminService.removeById(id));
//    }
//
//    @GetMapping
//    public Result findAll() {
//        return Result.success(adminService.list());
//    }



//    @GetMapping("/page") //分页查询 mybatis-plus
//    public Result findPage(@RequestParam int pageNum,
//                                @RequestParam int pageSize,
//                                 @RequestParam String admin) {
//
//        IPage<Admin> page = new Page<>(pageNum, pageSize);
//        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
//        queryWrapper.orderByDesc("id");
//        queryWrapper.like("admin", admin);
//        return Result.success(adminService.page(page, queryWrapper));
//    }
//
//    @DeleteMapping ("/del/batch")
//    public Result deleteBatch(@RequestBody List<Integer> ids) {
//        return Result.success(adminService.removeByIds(ids));
//    }
//
//    @GetMapping("/export")
//    public void export(HttpServletResponse response) throws Exception {
//        //从数据库查询出所有数据
//        List<Admin> list = adminService.list();
//
//        ExcelWriter writer = ExcelUtil.getWriter(true);
//
//        //对象写入excel，并输出标题
//        writer.write(list, true);
//
//        //设置浏览器相应的格式
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
//        String fileName = URLEncoder.encode("导出信息", "UTF-8");
//        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//
//        ServletOutputStream out = response.getOutputStream();
//        writer.flush(out, true);
//        out.close();
//        writer.close();
//    }
//
////导入
//    @PostMapping("/import")
//    public Result imp(MultipartFile file) throws Exception {
//        InputStream inputStream = file.getInputStream();
//        ExcelReader reader = ExcelUtil.getReader(inputStream);
//        List<Admin> list = reader.readAll(Admin.class);
//        return Result.success(adminService.saveBatch(list));
//    }
}

