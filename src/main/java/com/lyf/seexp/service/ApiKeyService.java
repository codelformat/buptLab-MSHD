package com.lyf.seexp.service;

public interface ApiKeyService {

    
    public String generateApiKey(Integer userId);

    public boolean validateApiKey(String apiKey);

}
