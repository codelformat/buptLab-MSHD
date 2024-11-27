package com.mshd.mshd.service;

import com.mshd.mshd.model.DisasterInfo;

public interface EarthquakeInfoService {
    /**
     * 保存编码后的地震信息到数据库
     * @param code 编码字符串
     * @return 是否保存成功
     */
    boolean saveEncodedInfo(String code);

    /**
     * 保存地震信息到数据库
     * @param info 地震信息对象
     * @return 是否保存成功
     */
    boolean saveInfo(DisasterInfo info);
} 