package com.xinyang.app.web.controller;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/xy/")
public class FileUploadController {



    @Autowired
    private XyProperties xyProperties;

    @PostMapping("upload")
    public String uploadAuth(MultipartFile file){

        try {
            return FileUtil.localUpload2(xyProperties.getFileConfig().getTempDir(),file.getOriginalFilename(),file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败！";
    }



}
