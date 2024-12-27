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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.lyf.seexp.service.DataBackupService;

@RestController
@RequestMapping("/event")
@Validated
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    private DataBackupService dataBackupService;

    // @PostMapping("/decode")
    // public Result decode(@Pattern(regexp = "^\\d{36}$", message = "Code must be
    // exactly 36 digits long") String code) {
    // try {
    // //Event event = eventService.decode(code);
    // eventService.addItemFromCode(code);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return Result.error("添加失败");
    // }
    // return Result.success();
    // }

    @PostMapping("/addOneTextItem")
    public Result addOneTextItem(@Pattern(regexp = "^\\d{36}$") String code, String description) {
        try {
            Event eventWithoutDescription = eventService.decode(code);
            eventWithoutDescription.setDescription(description);
            eventService.addItem(eventWithoutDescription);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加单条信息失败");
        }
        return Result.success();
    }

    // 读取xls,添加多个条目
//    @PostMapping("/addXlsTextItem")
//    public Result addXlsTextItem() {
//        try {
//            ArrayList<Event> events = eventService.readXlsFile();
//            for (Event event : events) {
//                eventService.addItem(event);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("xls读取失败");
//        }
//        return Result.success();
//    }

    @PostMapping("/upload")
    public Result addFile(@RequestParam MultipartFile file) {
        String url = new String();
        try {
            url = AliOSSUtils.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
        return Result.success(url);
    }

    // 读取xlsx,添加多个条目
    @PostMapping("/addXlsTextItems")
    public Result addXlsxTextItems(@RequestParam MultipartFile file) {
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
            for (Event event : events) {
                eventService.addItem(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("xls读取失败");
        }
        return Result.success("xlsx读取成功");
    }

    @GetMapping("/list")
    public Result getEventList(@RequestParam(required = false) String search) {
        try {
            List<Event> events;
            if (search != null && !search.trim().isEmpty()) {
                events = eventService.searchEvents(search.trim());
            } else {
                events = eventService.getEventList();
            }
            return Result.success(events);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取事件列表失败");
        }
    }

    @PostMapping("/addPics")
    public Result addPicItems(@RequestParam MultipartFile file) {
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
                
                if (!code.matches("^\\d{36}$")) {
                    return Result.error("无效的灾情编码格式");
                }
                
                Event eventWithoutDescription = eventService.decode(code);
                eventWithoutDescription.setDescription(imageUrl);
                eventService.addItem(eventWithoutDescription);
                return Result.success("图片成功存入数据库");
            } else {
                return Result.error("编码为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/code/{code}")
    public Result deleteEvent(@PathVariable String code) {
        try {
            eventService.deleteByCode(code);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }

    @PutMapping("/code/{code}")
    public Result updateEvent(@PathVariable String code, @RequestBody Event event) {
        try {
            if (!code.equals(event.getCode())) {
                return Result.error("编码不匹配");
            }
            eventService.updateByCode(event);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败");
        }
    }

    @GetMapping("/backup/list")
    public Result listBackupEvents(@RequestParam(required = false) String search) {
        try {
            List<Event> events = dataBackupService.searchBackupEvents(search);
            return Result.success(events);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/backup/trigger")
    public Result triggerBackup() {
        try {
            dataBackupService.performBackup();
            return Result.success("数据备份成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("数据备份失败: " + e.getMessage());
        }
    }

    @PostMapping("/backup/reset")
    public Result resetBackup() {
        try {
            dataBackupService.resetBackupSchema();
            return Result.success("备份表重置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("备份表重置失败: " + e.getMessage());
        }
    }

    @GetMapping("/backup/time-window")
    public Result getBackupTimeWindow() {
        try {
            Integer days = dataBackupService.getTimeWindow();
            return Result.success(days);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取时间窗口失败: " + e.getMessage());
        }
    }

    @PostMapping("/backup/time-window")
    public Result setBackupTimeWindow(@RequestParam(required = false) Integer days, @RequestBody(required = false) TimeWindowRequest request) {
        try {
            // Get days from either request param or request body
            Integer finalDays = days;
            if (finalDays == null && request != null) {
                finalDays = request.getDays();
            }
            
            if (finalDays == null) {
                return Result.error("时间窗口不能为空");
            }
            
            if (finalDays <= 0) {
                return Result.error("时间窗口必须大于0天");
            }
            
            dataBackupService.setTimeWindow(finalDays);
            return Result.success("时间窗口设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("设置时间窗口失败: " + e.getMessage());
        }
    }

    @PostMapping("/addEventWithMedia")
    public Result addEventWithMedia(
            @Pattern(regexp = "^\\d{36}$", message = "灾情码必须是36位数字") @RequestParam String code,
            @RequestParam MultipartFile file) {
        try {
            // 1. 上传文件到OSS
            String fileUrl = AliOSSUtils.upload(file);
            if (fileUrl == null) {
                return Result.error("文件上传失败");
            }

            // 2. 解析灾情码
            Event event = eventService.decode(code);
            if (event == null) {
                return Result.error("灾情码解析失败");
            }

            // 3. 设置描述（文件URL）
            event.setDescription(fileUrl);

            // 4. 保存到数据库
            eventService.addItem(event);

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理失败: " + e.getMessage());
        }
    }

    // Add TimeWindowRequest static class
    private static class TimeWindowRequest {
        private Integer days;
        
        public Integer getDays() {
            return days;
        }
        
        public void setDays(Integer days) {
            this.days = days;
        }
    }
}
