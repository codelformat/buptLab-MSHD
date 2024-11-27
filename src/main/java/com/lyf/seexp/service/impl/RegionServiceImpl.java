package com.lyf.seexp.service.impl;

import com.lyf.seexp.mapper.RegionMapper;
import com.lyf.seexp.pojo.Region;
import com.lyf.seexp.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl implements RegionService {
    @Autowired
    private RegionMapper regionMapper;

    @Override
    public Region getRegionByCode(String code) {
        regionMapper.findByCode(code);
        return null;
    }
}
