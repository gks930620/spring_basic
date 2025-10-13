package com.security.jwt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAuthorizationRequestRepository implements
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    
    private final Map<String, OAuth2AuthorizationRequest> authorizationRequests = new ConcurrentHashMap<>();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String state = request.getParameter("state");
        if (state == null) {
            return null;
        }
        return authorizationRequests.get(state);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            return;
        }
        
        String state = authorizationRequest.getState();
        authorizationRequests.put(state, authorizationRequest);

        System.out.println("✅ OAuth2AuthorizationRequest 저장: " + state);

        // 5분 후 자동 삭제 (보안 상 Authorization Request를 계속 들고 있을 필요 없음)
        new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(5);
                authorizationRequests.remove(state);
                System.out.println("🗑️ OAuth2AuthorizationRequest 만료 삭제: " + state);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,HttpServletResponse response) {
        String state = request.getParameter("state");
        if (state == null) {
            return null;
        }
        System.out.println("🚀 OAuth2AuthorizationRequest 조회 후 삭제: " + state);
        return authorizationRequests.remove(state);
    }
}