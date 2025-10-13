package com.example.security.config;

import com.example.security.service.CustomOAuth2UserService;
import com.example.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;


    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // H2 콘솔 CSRF 비활성화
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // H2 콘솔을 iframe에서 허용
        // 이 설정은 내부 H2DB 사용을 위한 설정일뿐 의미없음.


        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/images/**").permitAll()  // 정적 자원 허용
            );


        http.authorizeHttpRequests((auth) -> auth
            .requestMatchers("/join/**", "/login").permitAll()
            .requestMatchers("/admin").hasAuthority("ADMIN")
            .requestMatchers("/my/**").hasAnyAuthority("ADMIN", "USER")
            .anyRequest().authenticated()
        );

        http.formLogin(auth -> auth
            .loginPage("/login")
            .loginProcessingUrl("/loginProc")  //기본값은 /login  (POST방식)
            .defaultSuccessUrl("/")
            .failureUrl("/login?error=true") // 로그인 실패 시 다시 로그인 페이지로.  기본값이 /login?error 지만  명시적으로.
            .permitAll()
        );

        
        http.userDetailsService(customUserDetailsService); //명시적으로 등록. 일반 form 로그인사용자처리

        http.oauth2Login(oauth2 -> oauth2
            .loginPage("/login")       // 한 페이지에서 폼 카카오 다 할거면 form이랑 같은게 좋음
            .defaultSuccessUrl("/") // 로그인 성공 후 이동할 URL
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService) // 명시적.   OAuth2 사용자 로그인 처리
            )
        );
        http.logout((auth) -> auth.logoutUrl("/logout")
            .logoutSuccessUrl("/"));

        http.csrf((auth) -> auth.disable());   //csrf 적용X
        return http.build();
    }
}