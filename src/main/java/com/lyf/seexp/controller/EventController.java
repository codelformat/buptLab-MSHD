package com.lyf.seexp.controller;

import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.pojo.Result;
import com.lyf.seexp.service.EventService;
import com.lyf.seexp.utils.AliOSSUtils;
import com.lyf.seexp.utils.ImgbbUtils;
import jakarta.validation.constraints.Pattern;
import lombok.val;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

@RestController
@RequestMapping("/event")
@Validated
public class EventController {
    @Autowired
    private EventService eventService;

//    @PostMapping("/decode")
//    public Result decode(@Pattern(regexp = "^\\d{36}$", message = "Code must be exactly 36 digits long") String code) {
//        try {
//            //Event event = eventService.decode(code);
//            eventService.addItemFromCode(code);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("添加失败");
//        }
//        return Result.success();
//    }

    @PostMapping("/addOneTextItem")
    public Result addOneTextItem(@Pattern(regexp = "^\\d{36}$") String code,String description)
    {
        try{
            Event eventWithoutDescription = eventService.decode(code);
            eventWithoutDescription.setDescription(description);
            eventService.addItem(eventWithoutDescription);
        }
        catch(Exception e){
            e.printStackTrace();
            return Result.error("添加单条信息失败");
        }
        return Result.success();
    }

    //读取xls,添加多个条目
    @PostMapping("/addXlsTextItem")
    public Result addXlsTextItem(){
        try {
            ArrayList<Event> events = eventService.readXlsFile();
            for(Event event:events){
                eventService.addItem(event);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error("xls读取失败");
        }
        return Result.success();
    }

    @PostMapping("/upload")
    public Result addFile(@RequestParam MultipartFile file)
    {
        String url = new String();
        try {
            url = AliOSSUtils.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
        return Result.success(url);
    }

    //读取xlsx,添加多个条目
    @PostMapping("/addXlsTextItems")
    public Result addXlsxTextItems(@RequestParam MultipartFile file){
        String fileUrl = new String();
        try {
            fileUrl = AliOSSUtils.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
        try {
            // 创建URL对象
            URL url = new URL(fileUrl);
            // 打开URL连接
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            ArrayList<Event> events = eventService.readXlsxFile(inputStream);
            for(Event event:events){
                eventService.addItem(event);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error("xls读取失败");
        }
        return Result.success("xlsx读取成功");
    }
@PostMapping("/addPics")
public Result addPicItems(@RequestParam MultipartFile file){
    try {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        // 调用工具类上传图片到 Imgbb
        String result = ImgbbUtils.uploadToImgbb(file);

        if (result != null) {
            // 分离 URL 和文件名
            String[] parts = result.split("@filename=");
            String imageUrl = parts[0];
            String code = parts.length > 1 ? parts[1] : "unknown-id";
            Event eventWithoutDescription = eventService.decode(code);
            eventWithoutDescription.setDescription(imageUrl);
            eventService.addItem(eventWithoutDescription);
        } else {
            return Result.error("id为空");
        }
    } catch (Exception e) {

        return Result.error("数据库错误");
    }
    return Result.success("iamge成功存入数据库");
}
}
