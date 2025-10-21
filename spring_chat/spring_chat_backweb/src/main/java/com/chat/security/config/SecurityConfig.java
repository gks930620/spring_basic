package com.chat.security.config;

import com.chat.security.JwtUtil;
import com.chat.security.filter.JwtAccessTokenCheckAndSaveUserInfoFilter;
import com.chat.security.filter.JwtLoginFilter;
import com.chat.security.repository.RefreshRepository;
import com.chat.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;  //내가 빈으로 등록한것들

    private final AuthenticationConfiguration authenticationConfiguration;  //authenticationManger를 갖고있는 빈.
    private final RefreshRepository refreshRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http  //내부H2DB  확인용.  진짜 1도 안중요함.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // H2 콘솔 CSRF 비활성화
            .headers(
                headers -> headers.frameOptions(frame -> frame.disable())); // H2 콘솔을 iframe에서 허용

        http    //기본 session방식관련 다 X
            .csrf(csrf -> csrf.disable())
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())  //기본 로그아웃 사용X
            .httpBasic(basic -> basic.disable());



        http   //경로와 인증/인가 설정.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/loginPage" ,  "/api/refresh/reissue" , "/home" , "/" , "/rooms" ,"/room/**"
                              ,"/ws-chat" , "/ws-chat/**",
                              // Swagger / OpenAPI 문서 및 UI
                              "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                ).permitAll()
                // ws 연결 자체는 http 통신이기때문에 허용. 다만 인증 자체는 ws에서 한게 아니라stomp 첫 연결시에 하는걸로 함.
                .requestMatchers("/api/me").authenticated()
                .requestMatchers("/api/rooms", "/api/room/**").authenticated()
                .requestMatchers("/api/logout").authenticated()  //security 기본 로그아웃 url인 /logout은 사용X
            );



        http          //필터
            .userDetailsService(customUserDetailsService)
            .addFilterAt(
                new JwtLoginFilter(authenticationConfiguration.getAuthenticationManager(), jwtUtil,
                    refreshRepository),
                UsernamePasswordAuthenticationFilter.class)  //기존 세션방식의 로그인 검증필터 대체.
            .addFilterBefore(
                new JwtAccessTokenCheckAndSaveUserInfoFilter(jwtUtil, customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class);

       
        http
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String errorCause =
                        request.getAttribute("ERROR_CAUSE") != null ? (String) request.getAttribute(
                            "ERROR_CAUSE") : null;
                    //인증없이(access token없이) 인증필요한 곳에 로그인했을 떄.
                    if (errorCause == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
                        return;
                    }
                    // JwtAccessTokenCheckAndSaveUserInfoFilter  토큰체크하는부분
                    if (errorCause.equals("토큰만료")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"error\": \"Access Token expired\"}");
                        return;
                    }
                    if (errorCause.equals("로그인실패")) { //jwtLoginFilter 로그인시도부분.
                        response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED); //로그인실패도 401로 하는게 보통
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"error\": \"아이디 비번 틀림.\"}");
                        return;
                    }
                })
            );
        return http.build();


    }
}