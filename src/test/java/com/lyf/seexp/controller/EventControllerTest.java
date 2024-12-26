package com.lyf.seexp.controller;

import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.pojo.Result;
import com.lyf.seexp.service.EventService;
import com.lyf.seexp.utils.AliOSSUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
//import org.junit.runner.RunWith;
import org.mockito.Mockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.powermock.api.mockito.PowerMockito.*;

//@RunWith(PowerMockRunner.class)
@WebMvcTest(EventController.class)
// @PrepareForTest(AliOSSUtils.class) // 指定需要处理的静态类
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private AliOSSUtils aliOSSUtils;

    // 测试 /addOneTextItem 接口
    @Test
    void testAddOneTextItem() throws Exception {
        // Mock EventService 的 decode 方法
        Event mockEvent = new Event();
        mockEvent.setCode("110101001005202105220204001010302001");
        when(eventService.decode(Mockito.anyString())).thenReturn(mockEvent);

        // 发起请求
        mockMvc.perform(post("/event/addOneTextItem")
                .param("code", "110101001005202105220204001010302001")
                .param("description", "Test Description")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0)) // 验证 Result 的 code 为 0
                .andExpect(jsonPath("$.message").value("操作成功")); // 验证 Result 的 message 为 "操作成功"
    }

    // 测试 /addXlsTextItem 接口
    @Test
    void testAddXlsTextItem() throws Exception {
        // Mock EventService 的 readXlsFile 方法
        ArrayList<Event> mockEvents = new ArrayList<>();
        mockEvents.add(new Event(
                "110101001005202105220204001010302001", // code
                "Location1", // location
                new Timestamp(System.currentTimeMillis()), // time
                "Category1", // sourceCategory
                "Subcategory1", // sourceSubcategory
                "Carrier1", // carrier
                "Disaster1", // disasterCategory
                "SubDisaster1", // disasterSubcategory
                "Indicator1", // disasterIndicator
                "Description1" // description
        ));
        mockEvents.add(new Event(
                "123456789012345678901234567890123457", // code
                "Location2", // location
                new Timestamp(System.currentTimeMillis()), // time
                "Category2", // sourceCategory
                "Subcategory2", // sourceSubcategory
                "Carrier2", // carrier
                "Disaster2", // disasterCategory
                "SubDisaster2", // disasterSubcategory
                "Indicator2", // disasterIndicator
                "Description2" // description
        ));
        when(eventService.readXlsFile()).thenReturn(mockEvents);

        // 发起请求
        mockMvc.perform(post("/event/addXlsTextItem")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0)) // 验证 Result 的 code 为 0
                .andExpect(jsonPath("$.message").value("操作成功")); // 验证 Result 的 message 为 "操作成功"
    }
    // @Test
    // void testSuccessfulFileUpload() throws IOException {
    // // 准备测试数据
    // String expectedUrl = "https://example.com/uploaded-file.jpg";
    // MultipartFile mockFile = new MockMultipartFile(
    // "file",
    // "test-file.jpg",
    // "image/jpeg",
    // "test file content".getBytes()
    // );

    // // 模拟OSS上传方法
    // when(AliOSSUtils.upload(mockFile)).thenReturn(expectedUrl);

    // // 执行测试
    // Result result = controller.addFile(mockFile);

    // // 验证结果
    // assertNotNull(result);
    // assertTrue(result.isSuccess());
    // assertEquals(expectedUrl, result.getData());

    // // 验证upload方法被调用
    // verify(AliOSSUtils, times(1)).upload(mockFile);
    // }
    // 测试 /upload 接口
    @Test
    void testUploadFile() throws Exception {
        try (MockedStatic<AliOSSUtils> mockedStatic = Mockito.mockStatic(AliOSSUtils.class)) {
            // 在这个 try 块内可以安全地 mock 静态方法
            mockedStatic.when(() -> AliOSSUtils.upload(any(MultipartFile.class)))
                    .thenReturn("http://mock-url.com/test.xlsx");

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "content".getBytes());

            mockMvc.perform(multipart("/event/upload")
                    .file(mockFile))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.message").value("操作成功"));
        }

    }

    // 测试 /addXlsTextItems 接口
    @Test
    void testAddXlsxTextItems() throws Exception {
        try (MockedStatic<AliOSSUtils> mockedStatic = Mockito.mockStatic(AliOSSUtils.class)) {

            // 模拟文件上传和 EventService 的行为
            when(AliOSSUtils.upload(any(MockMultipartFile.class))).thenReturn("http://mock-url.com/test.xlsx");
            ArrayList<Event> mockEvents = new ArrayList<>();
            mockEvents.add(new Event(
                    "123456789012345678901234567890123456", // code
                    "Location1", // location
                    new Timestamp(System.currentTimeMillis()), // time
                    "Category1", // sourceCategory
                    "Subcategory1", // sourceSubcategory
                    "Carrier1", // carrier
                    "Disaster1", // disasterCategory
                    "SubDisaster1", // disasterSubcategory
                    "Indicator1", // disasterIndicator
                    "Description1" // description
            ));
            mockEvents.add(new Event(
                    "123456789012345678901234567890123457", // code
                    "Location2", // location
                    new Timestamp(System.currentTimeMillis()), // time
                    "Category2", // sourceCategory
                    "Subcategory2", // sourceSubcategory
                    "Carrier2", // carrier
                    "Disaster2", // disasterCategory
                    "SubDisaster2", // disasterSubcategory
                    "Indicator2", // disasterIndicator
                    "Description2" // description
            ));
            when(eventService.readXlsxFile(any())).thenReturn(mockEvents);

            MockMultipartFile mockFile = new MockMultipartFile("file", "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "content".getBytes());

            mockMvc.perform(multipart("/event/upload")
            .file(mockFile))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0)) // 验证 Result 的 code 为 0
                    .andExpect(jsonPath("$.message").value("操作成功")); // 验证 Result 的 message 为 "操作成功"
        }
    }
}
