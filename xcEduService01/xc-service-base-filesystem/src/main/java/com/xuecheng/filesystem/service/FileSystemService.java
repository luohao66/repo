package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileSystemService {

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;

    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;

    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;

    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;

    public UploadFileResult uploadFlie(MultipartFile multipartFile, String filetag, String businesskey, String metadata){

        if(multipartFile==null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }

        //将文件上传到fastDFS中 并得到fileId
        String flieId = uploadFlie(multipartFile);
        if(StringUtils.isEmpty(flieId)){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //将文件Id储存到mongdb数据库
        FileSystem fileSystem=new FileSystem();
        fileSystem.setFileId(flieId);
        fileSystem.setFilePath(flieId);
        fileSystem.setFiletag(filetag);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        if(StringUtils.isNotEmpty(metadata)){
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }




    //将文件上传到fastDFS中 并得到fileId
    private String uploadFlie(MultipartFile multipartFile){

        try {
            //初始化配置
            initFdfsConfig();
            //创建TrackerClient对象
            TrackerClient trackerClient=new TrackerClient();
            //连接tracker
            TrackerServer trackerServer= trackerClient.getConnection();
            //获取Stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建stroageClient对象
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeStorage);
            //将文件转化为字节数组
            byte[] bytes = multipartFile.getBytes();
            //获取文件原始名
            String originalFilename = multipartFile.getOriginalFilename();
            //再获取文件的扩展名
            String exname= originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //上传成功后得到fileId
            String fileId= storageClient1.upload_file1(bytes, exname, null);
            return fileId;
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void initFdfsConfig(){
        //初始化tracker
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
