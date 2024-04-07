package com.robot_server.springboot_delivery.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.robot_server.springboot_delivery.common.Constants;
import com.robot_server.springboot_delivery.common.Result;
import com.robot_server.springboot_delivery.entity.FileData;
import com.robot_server.springboot_delivery.mapper.FileMapper;
import com.robot_server.springboot_delivery.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;


@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("http://192.168.3.62:9090/file/")
    private String BaseUrl;

    @Resource
    private FileMapper fileMapper;

    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws Exception{
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        //存储磁盘
        File uploadParentFile = new File(fileUploadPath);
        if(!uploadParentFile.exists()) {
            uploadParentFile.mkdirs();
        }
        //定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        //拼接标识码和后缀
        String uid = uuid + StrUtil.DOT + type;

        //查询文件md5是否存在
        File uploadFile = new File(fileUploadPath + uid);

        String url;
        String md5;
        //上传到磁盘并获取md5
        file.transferTo(uploadFile);
        md5 = SecureUtil.md5(uploadFile);
        FileData dbFile = getFileByMd5(md5);
        //获取url
        if(dbFile != null) {
            url = dbFile.getUrl();
            //文件存在，删除上传文件
            uploadFile.delete();
        }
        else {
            url = BaseUrl + uid;
        }
        //存储数据库
        FileData fileData = new FileData();
        fileData.setFilename(originalFilename);
        fileData.setType(type);
        fileData.setSize(size / 1024); //KB
        fileData.setUrl(url);
        fileData.setMd5(md5);
        fileMapper.insert(fileData);

        return url;
    }

    /**
     * 文件下载接口
     * 文件上传接口返回的字符串对应该下载接口的链接
     * @param uid
     * @param response
     * @throws Exception
     */

    @GetMapping("/{uid}")
    public void download(@PathVariable String uid, HttpServletResponse response) throws Exception{
        //根据标识码获取文件
        File uploadFile = new File(fileUploadPath + uid);
        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(uid, "UTF-8"));
        response.setContentType("application/octet-stream");


        //读取上传字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    private FileData getFileByMd5(String md5) {
        QueryWrapper<FileData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<FileData> fileDataList= fileMapper.selectList(queryWrapper);
        return fileDataList.size() == 0 ? null : fileDataList.get(0);
    }

    /**
     * 管理系统未确认添加完整的用户信息，但已上传了用户图像
     * 将文件从磁盘上删除
     */
    @DeleteMapping("/del")
    public Result delete(@RequestParam String url) {

        QueryWrapper<FileData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        int num = fileMapper.delete(queryWrapper);

        String[] list = url.split("/");
        String filename = list[list.length - 1];
        String filepath = fileUploadPath + filename;
        File file = new File(filepath);
        if(file.exists()) {
            file.delete();
            return Result.success("删除文件成功，删除的库记录条数：" + num);
        }
        return Result.error(Constants.CODE_402, "磁盘目录下未找到该文件，删除的库记录条数：" + num);
    }
}
