package com.security.jwt.controller;

import com.security.jwt.config.InMemoryAuthorizationRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/custom-oauth2/login")
@RequiredArgsConstructor
public class Oauth2LoginController {


    private final InMemoryAuthorizationRequestRepository authorizationRequestRepository;


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @RequestMapping("/kakao")
    public ResponseEntity<?> kakaoOauth2Login(HttpServletRequest request, HttpServletResponse response){
        String state=""+UUID.randomUUID();

        // ✅ OAuth2AuthorizationRequest 직접 생성 == 로그인사용자가 로그인페이지 요청한사용자인지 확인하는 용.  클라이언트 구별!!
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .clientId(kakaoClientId)
            .redirectUri(kakaoRedirectUri)
            .state(state)
            .attributes(attrs -> attrs.put("registration_id", "kakao"))
            .build();

        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        //기본은 session에 저장해서 구별하는건데 여기서는 session없으니까 그냥 map에다..  서버여러대면 DB 등

        // ✅ Authorization URL 생성
        String authorizationUrl = "https://kauth.kakao.com/oauth/authorize"
            + "?client_id=" + kakaoClientId
            + "&redirect_uri=" + kakaoRedirectUri
            + "&response_type=code"
            + "&state=" + state;

        return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl)); //client에는 카카오 restAPI문서에서 요구하는 형태로!
    }
}


