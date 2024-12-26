package com.lyf.seexp.service;

import com.lyf.seexp.entity.User;

public interface UserService {
    User registerUser(String username, String password) throws RuntimeException;
    User loginUser(String username, String password) throws RuntimeException;
    boolean existsByUsername(String username);
}
