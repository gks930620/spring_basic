package com.chat.security.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/api/me")
    public Map<String, String> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {  //사실 securityy에서 잘 처리할거임.
            return Map.of("error", "인증되지 않은 사용자입니다.");
        }
        return Map.of("username", userDetails.getUsername());
    }
}
