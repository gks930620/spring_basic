package com.security.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.JwtUtil;
import com.security.jwt.entity.RefreshEntity;
import com.security.jwt.model.CustomUserAccount;
import com.security.jwt.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private  final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserAccount customUserAccount = (CustomUserAccount) authentication.getPrincipal();


        String accessToken = jwtUtil.createAccessToken(customUserAccount.getUsername());
        String refreshToken=jwtUtil.createRefreshToken(customUserAccount.getUsername());


        // ✅ 새 Refresh Token 저장 (기존 Token 삭제 X)
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserEntity(customUserAccount.getUserEntity());  // 이전 필터에서 사용자정보저장할 떄  userEntity는  entity상태로 저장됨.
        refreshEntity.setToken(refreshToken);
        refreshRepository.save(refreshEntity);


        // 토큰을 응답에 포함
        response.setContentType("application/json");
//        response.getWriter().write("{\"access_token\": \"" + accessToken + "\"}");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
            Map.of("access_token", accessToken, "refresh_token", refreshToken)
        ));


    }
}