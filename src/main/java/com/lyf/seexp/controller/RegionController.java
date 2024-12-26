package com.lyf.seexp.controller;

import com.lyf.seexp.pojo.Result;
import com.lyf.seexp.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/region")
@Validated
public class RegionController {
    @Autowired
    private RegionService regionService;

    @PostMapping("/importRegions")
    public Result importRegions(@RequestParam("file") MultipartFile file) {
        try {
            String result = regionService.uploadAndImportRegions(file);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导入失败：" + e.getMessage());
        }
    }
}
