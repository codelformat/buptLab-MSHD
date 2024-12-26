package com.lyf.seexp.service.impl;


import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lyf.seexp.mapper.ApiKeyMapper;
import com.lyf.seexp.pojo.ApiKey;
import com.lyf.seexp.service.ApiKeyService;

@Service
public class ApiKeyServiceImpl implements ApiKeyService{

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    public String generateApiKey(Integer userId) {
        String apiKey = UUID.randomUUID().toString().replace("-", "");
        ApiKey entity = new ApiKey();
        entity.setUserId(userId);
        entity.setApiKey(apiKey);
        entity.setCreatedAt(new Date(System.currentTimeMillis()));
        entity.setExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 一周有效期
        entity.setIsActive(true);
        apiKeyMapper.insert(entity);
        return apiKey;
    }

    public boolean validateApiKey(String apiKey) {
        ApiKey keyEntity = apiKeyMapper.selectByApiKey(apiKey);
        return keyEntity != null && (keyEntity.getExpiresAt().after(new Date(System.currentTimeMillis()))||keyEntity.getIsPermanent() )&&keyEntity.getIsActive();
    }
}

