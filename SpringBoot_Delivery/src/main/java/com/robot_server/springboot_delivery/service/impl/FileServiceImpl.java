package com.robot_server.springboot_delivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot_server.springboot_delivery.entity.FileData;
import com.robot_server.springboot_delivery.mapper.FileMapper;
import com.robot_server.springboot_delivery.service.IFileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileData> implements IFileService {
}
