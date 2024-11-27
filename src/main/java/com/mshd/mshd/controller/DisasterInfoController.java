package com.mshd.mshd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mshd.mshd.model.DisasterInfo;
import com.mshd.mshd.service.DisasterCodeService;
import com.mshd.mshd.service.EarthquakeInfoService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/disaster")
@CrossOrigin
public class DisasterInfoController {

    @Autowired
    private DisasterCodeService disasterCodeService;

    @Autowired
    private EarthquakeInfoService earthquakeInfoService;

    /**
     * 将灾情信息编码
     * @param info 灾情信息对象
     * @return 编码结果
     */
    @PostMapping("/encode")
    public ResponseEntity<Map<String, String>> encode(@RequestBody DisasterInfo info) {
        String code = disasterCodeService.encode(info);
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return ResponseEntity.ok(response);
    }

    /**
     * 解码灾情信息
     * @param code 编码字符串
     * @return 解码后的灾情信息
     */
    @GetMapping("/decode/{code}")
    public ResponseEntity<DisasterInfo> decode(@PathVariable String code) {
        DisasterInfo info = disasterCodeService.decode(code);
        return ResponseEntity.ok(info);
    }

    /**
     * 保存编码后的灾情信息到数据库
     * @param code 编码字符串
     * @return 保存结果
     */
    @PostMapping("/save/encoded")
    public ResponseEntity<Map<String, Boolean>> saveEncoded(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        boolean success = earthquakeInfoService.saveEncodedInfo(code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    /**
     * 直接保存灾情信息到数据库
     * @param info 灾情信息对象
     * @return 保存结果
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Boolean>> save(@RequestBody DisasterInfo info) {
        boolean success = earthquakeInfoService.saveInfo(info);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }
} 