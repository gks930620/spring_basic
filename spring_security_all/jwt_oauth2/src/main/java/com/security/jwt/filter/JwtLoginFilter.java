package com.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.JwtUtil;
import com.security.jwt.entity.RefreshEntity;
import com.security.jwt.model.CustomUserAccount;
import com.security.jwt.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



//  /login URL일 때 동작  , oauth2로그인이랑은 상관없음!
@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private  final AuthenticationManager authenticationManager;  //new 로 생성하면 부모의 authenticationManager필드는 null이기 때문에 생성자로 주입.
    private final JwtUtil jwtUtil;

    private  final RefreshRepository refreshRepository;

    // 로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 요청에서 username, password 추출
            // jwt는 API서버 분리된방식.  username,password는  body에 포함되서 옴.
            // 파라미터에 포함되서 오지않음 보통.  이것때문에 재정의. UsernamePasswordAuthetnctionFilter는 parameter 를 처리함.
            Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(), HashMap.class);
            String username = credentials.get("username");
            String password = credentials.get("password");


            //이 부분은 UsernamePasswordAuthetnctionFilter 코드 그대로.
            // AuthenticationManger를 통해 확인하는건
            // 결국 username,password를 가지고 CustomUserDetailsService의 return값(CustomUserAccount)이랑 비교.
            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            this.setDetails(request, authRequest);
            return authenticationManager.authenticate(authRequest);  //여기서 AuthenticationException 발생.
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse authentication request", e);  //readValue하는과정에서 발생.
        }

    }

    // 로그인 성공 → JWT 토큰 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserAccount customUserAccount = (CustomUserAccount) authResult.getPrincipal();

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

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        request.setAttribute("ERROR_CAUSE" , "로그인실패"); //실패 후 config의 entryPoint로
        super.unsuccessfulAuthentication(request,response,failed);
    }

}

