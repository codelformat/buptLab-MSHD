package com.lyf.seexp.controller;

import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.pojo.Result;
import com.lyf.seexp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
@Validated
public class VisualController {
    @Autowired
   private EventService ES;
    @GetMapping("/getStats")
    public Result<Event> getStats() {
        List<Event> events=ES.getEventList();
        return Result.success(events);
    }
}
