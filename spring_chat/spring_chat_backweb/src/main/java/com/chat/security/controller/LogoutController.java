package com.chat.security.controller;



//Login은 /login  => JwtLoginFilter에서 처리


import com.chat.security.JwtUtil;
import com.chat.security.service.RefreshService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LogoutController {
    private final JwtUtil jwtUtil;
    private final RefreshService refreshService;

    @RequestMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("RefreshToken") String refreshToken) {
        refreshService.deleteRefresh(refreshToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

}
