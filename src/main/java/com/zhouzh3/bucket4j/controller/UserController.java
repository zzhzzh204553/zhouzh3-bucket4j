package com.zhouzh3.bucket4j.controller;

import com.zhouzh3.bucket4j.core.annotation.RateLimit;
import com.zhouzh3.bucket4j.core.constants.RateLimitType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haig
 */
@RestController
@RequestMapping("/api/user")
public class UserController {



    @GetMapping("/{userId}")
    @RateLimit(type = RateLimitType.USER, key = "#userId")
    public ResponseEntity<String> queryUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body("添加用户%s成功！".formatted(userId));
    }
}