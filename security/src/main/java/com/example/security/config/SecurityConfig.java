package com.example.security.config;

import com.example.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration   //Spring에서 설정 파일임을 나타냄.
@EnableWebSecurity
//Spring Security의 보안 설정을 활성화하는 애너테이션(annotation)
// Spring Boot 2에서는 필수였지만, Spring Boot 3에서는 생략가능. but security설정임을 명시
public class SecurityConfig {

    @Bean //security는 password를 DB에 저장할 때 인코딩해서 저장.  비교할 때는 디코딩 후 비교.
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private final CustomUserDetailsService customUserDetailsService;
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // H2 콘솔 CSRF 비활성화
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // H2 콘솔을 iframe에서 허용

        http.authorizeHttpRequests((auth) -> auth
            .requestMatchers("/join/**", "/login").permitAll()    // 이 url들은 로그인 안해도됨
            .requestMatchers("/admin").hasAuthority("ADMIN")   //ADMIN 권한 사용자만
            .requestMatchers("/my/**").hasAnyAuthority("ADMIN", "USER")  // ADMIN, USER 권한 중 하나 가지면 ㅇㅋ
            .anyRequest().authenticated()   // 그 외 요청들은 로그인해야만..
        );


        http.formLogin((auth) -> auth
            .loginPage("/login")   // login페이지 URL 지정.  @RequestMapping("/login")을 만들어야한다.  => login.html
            .loginProcessingUrl("/loginProc")   // login.html에서  form태그의 action URL이  /loginProc여야한다.
                                                // @RequestMapping("/loginProc")는 없다. login과정은 security가 하기때문.
            .defaultSuccessUrl("/")             // 로그인 성공 후 redirect 되는 URL
            .failureUrl("/login?error=true") // 로그인 실패 시 다시 로그인 페이지로.  기본값이 /login?error 지만  명시적으로.
            .permitAll()
        );

        http.userDetailsService(customUserDetailsService); //명시적으로 등록

        http.logout((auth) -> auth.logoutUrl("/logout")   //   /logout으로 요청하면 logout이 된다.  @RM은 없다. 로그아웃도 security가 한다.
            .logoutSuccessUrl("/"));                     //    로그아웃 성공 후 /로 redirect


        http.csrf((auth) -> auth.disable());   //보안관련설정. 자세한 설명은 csrf 따로.
        return http.build();
    }
}
