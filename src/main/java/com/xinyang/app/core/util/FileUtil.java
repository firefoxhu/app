package com.xinyang.app.core.util;
import com.qiniu.common.QiniuException;
import com.xinyang.app.core.plugin.QiniuApi;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

public class FileUtil {

    public static String localUpload2(String tempPath,String fileName,InputStream inputStream){

        String todayPath = LocalDate.now().toString().replace("-","");

        String sourcePath = System.getProperty("user.dir") + tempPath + todayPath + "/";

        File file = new File(sourcePath);

        if(!file.exists()) {
            file.mkdir();
        }

        String destName = UUID.randomUUID().toString().replace("-","") + fileName.substring(fileName.lastIndexOf("."));

        try {
            FileUtils.copyToFile(inputStream,new File(sourcePath + destName));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return destName;
    }


    public static  void localUpload(String tempPath,String fileName) {

        String todayPath = LocalDate.now().toString().replace("-","");

        String sourcePath = System.getProperty("user.dir") + tempPath + todayPath + "/";

        String dest = sourcePath + fileName;
        try {

            // 生成缩略图
            Thumbnails.of(new File(dest)).scale(1.0).outputQuality(0.25).toFile(dest);

            // 压缩图片大小
            Thumbnails.of(dest).height(180).outputQuality(1.0).toFile((sourcePath + "S" + fileName));
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }

    }


    public static void qiNiuUpload(String filePath,String fileName){

        String todayPath = LocalDate.now().toString().replace("-","");

        String sourcePath = System.getProperty("user.dir") + filePath + todayPath + "/";

        try {
            //缩略图上传七牛云
            QiniuApi.uploadFile(sourcePath, fileName, QiniuApi.BUCKET_NAME);

            // 压缩图上传七牛云
            QiniuApi.uploadFile(sourcePath, ("S" + fileName), QiniuApi.BUCKET_NAME);
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }

        new File(sourcePath + fileName).delete();

        new File(sourcePath + "S" + fileName).delete();
    }


    public static void delete(String fileName){
        try {
            QiniuApi.deleteFile(QiniuApi.BUCKET_NAME,fileName);
        } catch (QiniuException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
