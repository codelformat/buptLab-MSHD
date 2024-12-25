package com.lyf.seexp.service;

import com.lyf.seexp.pojo.CodeInfo;
import com.lyf.seexp.pojo.Event;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface CodeInfoService {
      String getCode(CodeInfo codeInfo);
}
