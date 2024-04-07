package com.robot_server.springboot_delivery.utils;

/*
    代码生成器
    2023/1/8
 */

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        generate();
    }
    private static void generate() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/users?serverTimezone=GMT%2b8", "root", "robot")
                .globalConfig(builder -> {
                    builder.author("syh") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\idea_workspace\\SpringBoot_Delivery\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.robot_server.springboot_delivery") // 设置父包名
                            .moduleName(null) // 设置父包模块名,null为单斜杠
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\idea_workspace\\SpringBoot_Delivery\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.controllerBuilder().enableHyphenStyle() //开启驼峰转连字符
                                    .enableRestStyle(); //生成@RestController控制器，返回json
                    builder.addInclude("user") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                    builder.addInclude("robot");
                    builder.addInclude("admin");
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
