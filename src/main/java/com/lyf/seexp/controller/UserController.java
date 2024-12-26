package com.lyf.seexp.controller;

import com.lyf.seexp.entity.User;
import com.lyf.seexp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.registerUser(username, password);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("username", user.getUsername());
            response.put("avatarUrl", user.getAvatarUrl());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, 
                                 @RequestParam String password, 
                                 HttpSession session) {
        try {
            User user = userService.loginUser(username, password);
            session.setAttribute("user", user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", user.getUsername());
            response.put("avatarUrl", user.getAvatarUrl());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", user.getUsername(),
                "avatarUrl", user.getAvatarUrl()
            ));
        }
        return ResponseEntity.ok(Map.of("authenticated", false));
    }
}

