package com.lyf.seexp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyf.seexp.mapper.EventMapper;
import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.service.ApiKeyService;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private EventMapper dataMapper;

    @GetMapping("/fetch")
    public ResponseEntity<List<Event>> fetchData(@RequestHeader("API_KEY") String apiKey) {
        if (!apiKeyService.validateApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        //List<Event> data = dataMapper.selectList(null);
        List<Event> data = dataMapper.findAll();
        
        return ResponseEntity.ok(data);
    }
}
