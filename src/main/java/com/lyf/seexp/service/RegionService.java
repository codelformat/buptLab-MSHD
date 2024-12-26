package com.lyf.seexp.service;

import com.lyf.seexp.pojo.Region;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

public interface RegionService {
    Region getRegionByCode(String code);
    
    // 从Excel文件读取并导入region数据
    List<Region> importRegionsFromExcel(InputStream inputStream) throws Exception;
    
    // 上传Excel文件并导入数据
    String uploadAndImportRegions(MultipartFile file) throws Exception;
}
