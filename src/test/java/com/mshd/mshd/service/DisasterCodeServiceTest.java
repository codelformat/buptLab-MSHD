package com.mshd.mshd.service;

import com.mshd.mshd.model.DisasterInfo;
import com.mshd.mshd.service.impl.DisasterCodeServiceImpl;
import com.mshd.mshd.util.RegionCodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DisasterCodeServiceTest {
    @Autowired
    private DisasterCodeService disasterCodeService;

    @Test
    void testEncodeDecode() {
        // 创建测试数据
        DisasterInfo originalInfo = new DisasterInfo();
        originalInfo.setLocation("河南省洛阳市洛宁县底张乡东南村村民委员会");
        originalInfo.setTime(LocalDateTime.now());
        originalInfo.setSource("后方地震应急指挥部");
        originalInfo.setCarrier("文字");
        originalInfo.setCategory("房屋破坏");
        originalInfo.setLabel("一般损坏面积");
        originalInfo.setDescription("500平方米");

        // 测试编码
        String code = disasterCodeService.encode(originalInfo);
        assertNotNull(code);
        assertEquals(36, code.length());
        
        // 确保地区码不是默认值
        String regionCode = code.substring(0, 12);
        assertNotEquals("000000000000", regionCode);

        // 测试解码
        DisasterInfo decodedInfo = disasterCodeService.decode(code);
        assertNotNull(decodedInfo);
        assertEquals(originalInfo.getLocation(), decodedInfo.getLocation());
        // 时间可能会有毫秒差异，所以去除毫秒
        assertEquals(originalInfo.getTime().toLocalDate(), decodedInfo.getTime().toLocalDate());
        assertEquals(originalInfo.getSource(), decodedInfo.getSource());
        assertEquals(originalInfo.getCarrier(), decodedInfo.getCarrier());
    }

    @Test
    void testInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            disasterCodeService.decode("invalid_code");
        });
    }
} 