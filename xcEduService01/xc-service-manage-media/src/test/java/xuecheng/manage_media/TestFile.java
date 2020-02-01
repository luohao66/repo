package xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFile {


    //文件分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sounceFile=new File("E:\\ffmpeng-test\\video\\lucene.avi");
        //块文件目录
        String chunkFileFolder="E:\\ffmpeng-test\\chunks\\";
        //定义块文件大小
        long chunkFileSize=1*1024*1024;

        //块数
        long chunkFileNum=(long) Math.ceil(sounceFile.length()*1.0/chunkFileSize);

        //创建读取文件的对象
        RandomAccessFile raf_read=null;
        try {
           raf_read=new RandomAccessFile(sounceFile,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] b=new byte[1024];
        for(int i=0;i<chunkFileNum;i++){

            File chunKFile=new File(chunkFileFolder+i);
            int len=-1;

            while ((len=raf_read.read(b))!=-1){
              //创建向块文件的写对象
                RandomAccessFile raf_write=new RandomAccessFile(chunKFile,"rw");
                raf_write.write(b,0,len);
                //如果块件的大小达到 1m开始写下一块
                if(chunKFile.length()>=chunkFileSize){
                    break;
                }

            }
        }
    }

    //块文件合并
    @Test
    public void testMergeFile() throws IOException {
        //块文件合并
        String chunkFileFolderPath="E:\\ffmpeng-test\\chunks\\";
        //块文件目录对象
        File chunkFileFolder=new File(chunkFileFolderPath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //按名排序
        List<File> filesList= Arrays.asList(files);
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });

        //合并文件
        File mergeFile=new File("E:\\ffmpeng-test\\ccf.avi");

        boolean newFile = mergeFile.createNewFile();

        RandomAccessFile raf_write=new RandomAccessFile(mergeFile,"rw");

        byte[] b =new byte[1024];
        for (File file : filesList) {
            RandomAccessFile raf_read=new RandomAccessFile(file,"r");
            int len=-1;
            while ((len=raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
