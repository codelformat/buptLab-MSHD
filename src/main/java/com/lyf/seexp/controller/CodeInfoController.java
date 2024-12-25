package com.lyf.seexp.controller;

import com.lyf.seexp.pojo.CodeInfo;
import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.pojo.Result;
import com.lyf.seexp.service.CodeInfoService;
import com.lyf.seexp.service.EventService;
import com.lyf.seexp.utils.AliOSSUtils;
import com.lyf.seexp.utils.ImgbbUtils;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/codeInfo")
@Validated
public class CodeInfoController {
    @Autowired
    private EventService eventService;
    @Autowired
    private CodeInfoService codeInfoService;

    // @PostMapping("/decode")
    // public Result decode(@Pattern(regexp = "^\\d{36}$", message = "Code must be
    // exactly 36 digits long") String code) {
    // try {
    // //Event event = eventService.decode(code);
    // eventService.addItemFromCode(code);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return Result.error("添加失败");
    // }
    // return Result.success();
    // }
    @PostMapping("/encode")
    public Result encode(@RequestBody CodeInfo codeInfo){
        String code = codeInfoService.getCode(codeInfo);
        return Result.success(code);
    }


}
