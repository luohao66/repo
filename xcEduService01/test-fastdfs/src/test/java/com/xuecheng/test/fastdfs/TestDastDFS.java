package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDastDFS {


    //文件上传
    @Test
    public void test(){

        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建trackerClient对象
            TrackerClient trackerClient=new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient对象
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeStorage);
            //向stroage服务器上传文件
            //从本地上传图片
            String path="D:/logo.png";
            //上传成功后返回的fileId
            String fileId = storageClient1.upload_file1(path, "png", null);
            System.out.println(fileId);
        } catch (Exception e) {
             e.printStackTrace();
        }
    }

    //文件下载
    @Test
    public void downflieTest(){
        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建trackerClient对象
            TrackerClient trackerClient=new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient对象
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeStorage);
            //向stroage服务器下载文件
            byte[] bytes = storageClient1.download_file1("group1/M00/00/00/wKgZhV4J3NqARzy_AAAawU0ID2Q426.png");
            //使用输出流保存文件
            FileOutputStream fileOutputStream=new FileOutputStream(new File("D:/logo.png"));
            fileOutputStream.write(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     //查询文件
    @Test
    public void queryFileTest() {

        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建trackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取stroage
      /*      StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);*/
            //创建storageClient对象
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);
            //查询文件
            FileInfo fileInfo = storageClient1.query_file_info1("group1/M00/00/00/wKgZhV4J3NqARzy_AAAawU0ID2Q426.png");
            System.out.println(fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
