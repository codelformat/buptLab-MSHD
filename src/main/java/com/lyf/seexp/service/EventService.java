package com.lyf.seexp.service;

import com.lyf.seexp.pojo.Event;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface EventService {
    Event decode(String encodedEvent);
    void addItem(Event event);
    void addItemFromCode(String eventCode);

    //上传文字信息
    void addTextItem(String eventCode, String text);

    //返回xlsx文件中的 所有Event(文件在内部写死)
    ArrayList<Event> readXlsFile();

    //上传文件
    String uploadFile(MultipartFile multipartFile);

    ArrayList<Event> readXlsxFile(InputStream inputStream);

    // 添加获取事件列表的方法
    List<Event> getEventList();

    void deleteByCode(String code);
    void updateByCode(Event event);

    // 添加搜索方法
    List<Event> searchEvents(String query);
}
