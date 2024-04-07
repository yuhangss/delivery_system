package com.robot_server.springboot_delivery.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.*;
import com.robot_server.springboot_delivery.mapper.DeliveryMapper;
import com.robot_server.springboot_delivery.mapper.UserMapper;
import com.robot_server.springboot_delivery.service.IDeliveryService;
import com.robot_server.springboot_delivery.service.IMapService;
import com.robot_server.springboot_delivery.utils.CodeUtils;
import com.robot_server.springboot_delivery.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    @Resource
    private DeliveryMapper deliveryMapper;

    @Resource
    private IMapService mapService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IDeliveryService deliveryService;

    /**
     * 小程序端第一次向后端发送订单信息
     * @param delivery 发送的信息，应当包括sender/senderid/src三个基本信息
     * @return 返回Result，包括信息和数据
     */
    @PostMapping("/user/send")
    public Result sendOrder(@RequestBody Delivery delivery) {
        if(CodeUtils.checkSender(delivery)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        //地址校验，校验成功时将发件人地址存入订单信息
        Map map = mapService.checkLoc(delivery.getSrc());
        delivery.setSrc_uuid(map.getLoc_uuid());
        deliveryMapper.insert(delivery);

        //做订单筛选，将对应的订单号返回给小程序，后续可更改为用is_finished is_accepted直接选择，目前数据库数据无规则，无法直接查找插入订单
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender", delivery.getSender());
        queryWrapper.eq("senderid", delivery.getSenderid());
        queryWrapper.eq("src", delivery.getSrc());

        List<Delivery> deliveries = deliveryMapper.selectList(queryWrapper);
        Delivery order_Max = CodeUtils.selectROrder(deliveries);
        if (order_Max == null) {
            return Result.error(Constants.CODE_402, "订单为空，异常");
        }
        return Result.success(order_Max.getId());
    }

    /**
     * 小程序查询订单接口，综合下面4个接口
     */
    @PostMapping("/user/query")
    public Result queryOrder(@RequestParam String id) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Delivery delivery = deliveryService.getOne(queryWrapper);
        if(delivery == null) {
            return Result.error(Constants.CODE_400, "订单号错误");
        }
        return Result.success(delivery);
    }


    //====================================以下是机器人申请的接口====================================

    /**
     * 机器人获取订单接口
     * @return id最小的(最先提交的)订单
     * ?同时访问数据
     */
    @PostMapping("/robot")
    public Result getOrder(@RequestParam String name) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", "000");

        List<Delivery> deliveries = deliveryMapper.selectList(queryWrapper);
        if(deliveries.size() == 0) {
            return Result.error(Constants.CODE_402, "没有待处理订单");
        }
        Delivery delivery = CodeUtils.selectOrder(deliveries);
        assert delivery != null;
        delivery.setRobot_name(name);
        delivery.setState("100");

        UpdateWrapper<Delivery> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", delivery.getId());
        updateWrapper.eq("src", delivery.getSrc());

        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("studentid", delivery.getSenderid());
        String sex = userMapper.selectOne(queryWrapper1).getSex();

        deliveryMapper.update(delivery, updateWrapper);
        return Result.success(sex, CodeUtils.selectOrder(deliveries));
    }

    /**
     * 更新订单状态接口
     * @param id 订单号
     * @param state 订单状态
     */
    @PostMapping("/robot/state")
    public Result updateState(@RequestParam String state, @RequestParam String id) {
        UpdateWrapper<Delivery> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", Integer.parseInt(id));
        Delivery delivery = deliveryService.getOne(updateWrapper);
        delivery.setState(state);
        return Result.success(deliveryMapper.update(delivery, updateWrapper));
    }

    /**
     * 到达发件人地址后，机器人先更新订单到达信息
     * @param delivery 更新的订单
     * @return 操作结果
     */
    @PostMapping("/robot/update")
    public Result updateOrder(@RequestBody Delivery delivery) {
        if(CodeUtils.checkSender(delivery)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }

        if(CodeUtils.checkReceiver(delivery)) {
            return Result.error(Constants.CODE_400, "无效参数");
        }

        //地址校验，校验成功时将收件人地址存入订单信息
        Map map = mapService.checkLoc(delivery.getDest());
        delivery.setDest_uuid(map.getLoc_uuid());

        UpdateWrapper<Delivery> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", delivery.getId());
        int tag = deliveryMapper.update(delivery, updateWrapper);
        if(tag < 1) {
            return Result.error(Constants.CODE_400, "更新订单失败");
        }
        return Result.success(delivery.getDest_uuid());
    }

    /**
     * 机器人模糊查询接口
     */
    @PostMapping("/robot/query")
    public Result FuzzyQuery(@RequestParam String receiver, @RequestParam String dest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //暂时根据姓名和房间号进行模糊查询
        queryWrapper.like(StringUtils.isNotBlank(receiver), "username", receiver);
        queryWrapper.like(StringUtils.isNotBlank(dest), "location", dest);
        List<User> users = userMapper.selectList(queryWrapper);

        if(users.size() == 0) {
            return Result.error(Constants.CODE_402, "未查询到相关人员，请重新输入用户信息或检查用户是否存在");
        }
        return Result.success("查询到以下用户，请选择一个用户", users);
    }


    //====================================以下是后端管理申请的接口====================================
    /**
     * 分页查询
     * 这里id接收类型为String，便于做模糊查询，如果前端传回空，则不作查找
     */
    @GetMapping("/manager/page")
    public IPage<Delivery> findPage(@RequestParam int pageNum,
                           @RequestParam int pageSize,
                           @RequestParam String id,
                           @RequestParam String sender) {
        IPage<Delivery> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        //如果前端id输入不是数字，返回空
        try {
            if(!id.equals("")) {
                queryWrapper.like("id", Integer.parseInt(id));
            }
            queryWrapper.like("sender", sender);
            queryWrapper.orderByDesc("id");
            Admin currentAdmin = TokenUtils.getCurrentAdmin();
            System.out.println("当前管理员=====================" + currentAdmin.getAdmin());
            return deliveryService.page(page, queryWrapper);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public boolean deleteOrder(@PathVariable int id) {
        return deliveryService.removeById(id);
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return deliveryService.removeByIds(ids);
    }

    /**
     * 用户信息导出接口，服务端调用
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        //从数据库查询出所有数据
        List<Delivery> list = deliveryService.list();

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
}
