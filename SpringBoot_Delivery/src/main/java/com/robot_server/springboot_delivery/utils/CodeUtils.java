package com.robot_server.springboot_delivery.utils;

import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.Delivery;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CodeUtils {


    /**
     * 获取id最小的订单
     * @param lists 获取到的所有未被接收订单
     * @return id最小的订单
     */
    public static Delivery selectOrder(List<Delivery> lists) {
        if(lists.size() == 0) {
            return null;
        }
        lists.sort(new Comparator<Delivery>() {
            @Override
            public int compare(Delivery o1, Delivery o2) {
                return o1.getId() - o2.getId();
            }
        });

        return lists.get(0);
    }

    /**
     * 获取id最大的订单
     * @param lists 所有匹配订单
     * @return id最大订单
     */
    public static Delivery selectROrder(List<Delivery> lists) {
        if(lists.size() == 0) {
            return null;
        }
        lists.sort(new Comparator<Delivery>() {
            @Override
            public int compare(Delivery o1, Delivery o2) {
                return o2.getId() - o1.getId();
            }
        });

        return lists.get(0);
    }

    /**
     * 检查订单的发件人信息是否填写
     * @param delivery 订单
     * @return true为填写错误，false为正确
     */
    public static boolean checkSender(Delivery delivery) {
        return Objects.equals(delivery.getSender(), "") || delivery.getSender() == null ||
                Objects.equals(delivery.getSenderid(), "") || delivery.getSenderid() == null ||
                Objects.equals(delivery.getSrc(), "") || delivery.getSrc() == null;
    }

    /**
     * 检查订单的收件人信息是否填写
     * @param delivery 订单
     * @return true为填写错误，false为正确
     */
    public static boolean checkReceiver(Delivery delivery) {
        return Objects.equals(delivery.getReceiver(), "") || delivery.getReceiver() == null ||
                Objects.equals(delivery.getReceiverid(), "") || delivery.getReceiverid() == null ||
                Objects.equals(delivery.getDest(), "") || delivery.getDest() == null;
    }
}
