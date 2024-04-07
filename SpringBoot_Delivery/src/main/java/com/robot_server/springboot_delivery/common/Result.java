package com.robot_server.springboot_delivery.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String code; //标志
    private String msg; //
    private Object data; //返回请求数据

    public static Result success() {
        return new Result(Constants.CODE_200, "", null);
    }

    public static Result success(String msg) {
        return new Result(Constants.CODE_200, msg, null);
    }

    public static Result success(String msg, Object data) {
        return new Result(Constants.CODE_200, msg, data);
    }

    public static Result success(Object data) {
        return new Result(Constants.CODE_200, "操作成功", data);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error() {
        return new Result(Constants.CODE_500, "系统错误!", null);
    }
}