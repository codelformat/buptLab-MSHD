package com.mshd.mshd.service;

import com.mshd.mshd.model.DisasterInfo;
import com.mshd.mshd.service.impl.DisasterCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DisasterCodeServiceTest {
    private DisasterCodeService disasterCodeService;

    @BeforeEach
    void setUp() {
        disasterCodeService = new DisasterCodeServiceImpl();
    }

    @Test
    void testEncodeDecode() {
        // 创建测试数据
        DisasterInfo originalInfo = new DisasterInfo();
        originalInfo.setLocation("青海省");
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

        // 测试解码
        DisasterInfo decodedInfo = disasterCodeService.decode(code);
        assertNotNull(decodedInfo);
        assertEquals(originalInfo.getLocation(), decodedInfo.getLocation());
        assertEquals(originalInfo.getTime(), decodedInfo.getTime());
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