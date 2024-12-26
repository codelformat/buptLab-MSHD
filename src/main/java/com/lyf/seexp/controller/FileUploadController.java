package com.lyf.seexp.controller;

import com.lyf.seexp.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.lyf.seexp.utils.AliOSSUtils;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        //把文件的内容存储到本地磁盘上
//        String originalFilename = file.getOriginalFilename();
//        //保证文件的名字是唯一的,从而防止文件覆盖
//        String filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
//        //file.transferTo(new File("C:\\Users\\Administrator\\Desktop\\files\\"+filename));
//        String url = AliOSSUtils.upload(filename,file.getInputStream());
         return Result.success();
    }
}
