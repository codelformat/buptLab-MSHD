package com.lyf.seexp.controller;

import com.lyf.seexp.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/region")
@Validated
public class RegionController {
    @Autowired
    private RegionService regionService;
}
