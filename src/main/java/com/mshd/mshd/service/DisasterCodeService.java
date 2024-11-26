package com.mshd.mshd.service;

import com.mshd.mshd.model.DisasterInfo;

public interface DisasterCodeService {
    /**
     * 编码灾情信息
     * @param info 灾情信息对象
     * @return 编码后的ID
     */
    String encode(DisasterInfo info);

    /**
     * 解码灾情信息
     * @param code 编码字符串
     * @return 解码后的灾情信息对象
     */
    DisasterInfo decode(String code);
} 