package com.lyf.seexp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lyf.seexp.service.ApiKeyService;

@RestController
@RequestMapping("/api/key")
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateApiKey(@RequestParam Integer userId) {
        String apiKey = apiKeyService.generateApiKey(userId);
        return ResponseEntity.ok(apiKey);
    }
}
