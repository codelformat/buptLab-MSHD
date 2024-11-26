package com.mshd.mshd.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegionCodeUtilTest {

    @Autowired
    private RegionCodeUtil regionCodeUtil;

    @Test
    void testGetRegionCode() {
        String location = "河南省洛阳市洛宁县底张乡东南村村民委员会";
        String code = RegionCodeUtil.getRegionCode(location);
        assertNotNull(code);
        assertEquals(12, code.length());
        assertEquals("410328213215", code);
    }

    @Test
    void testInvalidLocation() {
        String location = "不存在的地址";
        String code = RegionCodeUtil.getRegionCode(location);
        assertEquals("000000000000", code);
    }

    // @Test
    // void testCacheLoading() {
    //     assertTrue(regionCodeUtil.getCacheSize() > 0, 
    //         "Cache should contain region codes");
    // }

    // @Test
    // void testExcelFilesExist() throws IOException {
    //     PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    //     Resource[] resources = resolver.getResources("classpath:region_code_*.xls");
    //     assertTrue(resources.length > 0, "Should find at least one Excel file");
    // }
} 