// package com.lyf.seexp.service;

// import org.mockito.InjectMocks;
// import org.mockito.Mock;

// import org.springframework.web.multipart.MultipartFile;

// import com.lyf.seexp.mapper.EventMapper;
// import com.lyf.seexp.mapper.RegionMapper;
// import com.lyf.seexp.pojo.Event;
// import com.lyf.seexp.pojo.Region;
// import com.lyf.seexp.service.impl.EventServiceImpl;
// import com.lyf.seexp.utils.AliOSSUtils;

// // import static org.junit.Assert.assertNull;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.sql.Timestamp;
// import java.util.ArrayList;

// import org.junit.jupiter.api.Test;

// public class EventServiceImplTest {
//     @Mock
//     private EventMapper eventMapper;

//     @Mock
//     private RegionMapper regionMapper;

//     @InjectMocks
//     private EventServiceImpl eventService;

//     // 测试方法
//     @Test
//     void testDecode() {
//         // 测试逻辑
//     }

//     @Test
//     void testToTimestamp_ValidInput() {
//         // Arrange
//         String validTime = "20231128123045";
//         Timestamp expectedTimestamp = Timestamp.valueOf("2023-11-28 12:30:45");

//         // Act
//         Timestamp result = eventService.to_timestamp(validTime);

//         // Assert
//         assertEquals(expectedTimestamp, result);
//     }

//     @Test
//     void testToTimestamp_InvalidInput() {
//         // Arrange
//         String invalidTime = "invalid";

//         // Act & Assert
//         assertNull(eventService.to_timestamp(invalidTime));
//     }

//     @Test
//     void testDecode_ValidCode() {
//         // Arrange
//         String validCode = "11000000000120231128123045100100";
//         Region mockRegion = new Region();
//         mockRegion.setName("Test Region");
//         when(regionMapper.findByCode("110000000001")).thenReturn(mockRegion);

//         // Act
//         Event event = eventService.decode(validCode);

//         // Assert
//         assertNotNull(event);
//         assertEquals("Test Region", event.getLocation());
//         assertEquals("业务报送数据", event.getSourceCategory());
//         assertEquals("前方地震应急指挥部", event.getSourceSubcategory());
//     }

//     @Test
//     void testDecode_InvalidRegion() {
//         // Arrange
//         String invalidCode = "00000000000020231128123045100100";
//         when(regionMapper.findByCode("000000000000")).thenThrow(new RuntimeException("Region not found"));

//         // Act
//         Event event = eventService.decode(invalidCode);

//         // Assert
//         assertNull(event.getLocation());
//     }

//     @Test
//     void testAddItem() {
//         // Arrange
//         Event event = new Event();
//         event.setCode("someCode");

//         // Act
//         eventService.addItem(event);

//         // Assert
//         verify(eventMapper, times(1)).add(event);
//     }

//     @Test
//     void testAddItemFromCode() {
//         // Arrange
//         String code = "11000000000120231128123045100100";
//         Event mockEvent = new Event();
//         when(eventMapper.add(any(Event.class))).thenReturn(1);

//         // Act
//         eventService.addItemFromCode(code);

//         // Assert
//         verify(eventMapper, times(1)).add(any(Event.class));
//     }

//     @Test
//     void testUploadFile() throws IOException {
//         // Arrange
//         MultipartFile mockFile = mock(MultipartFile.class);
//         when(mockFile.getOriginalFilename()).thenReturn("test.xlsx");
//         when(AliOSSUtils.upload(mockFile)).thenReturn("http://mock-url.com/test.xlsx");

//         // Act
//         String url = eventService.uploadFile(mockFile);

//         // Assert
//         assertEquals("http://mock-url.com/test.xlsx", url);
//     }

//     @Test
//     void testReadXlsxFile() throws IOException {
//         // Arrange
//         String mockExcelData = "ID\tDescription\n" +
//                 "11000000000120231128123045100100\tTest Event\n";
//         InputStream inputStream = new ByteArrayInputStream(mockExcelData.getBytes());

//         // Act
//         ArrayList<Event> events = eventService.readXlsxFile(inputStream);

//         // Assert
//         assertNotNull(events);
//         assertEquals(1, events.size());
//         assertEquals("Test Event", events.get(0).getDescription());
//     }

// }
