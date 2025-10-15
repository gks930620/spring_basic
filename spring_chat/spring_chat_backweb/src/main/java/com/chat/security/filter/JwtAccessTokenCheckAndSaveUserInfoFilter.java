package com.chat.security.filter;

import com.chat.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

//보통 이름은 JwtAuthorizationFilter를 한다.  
//이름을 잘 못 지어도  jwt관련 필터들이 정확히 무슨처리하는지 알면 덜 헷갈린다. 
// access token을 검증하고  CustomUserAccount(UserDetails)를 SecurityContext에 저장하는 필터
@RequiredArgsConstructor
public class JwtAccessTokenCheckAndSaveUserInfoFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;  //단순히  jwt기능제공
    private final UserDetailsService userDetailsService;  //내가만들고 빈 등록한 CustomUserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain)
        throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        //refresh든 access든  authroization header로 보낸다고 가정함.
        // 보내는 방식이 다르면 거기에 맞춰 따로 token얻는 방법을 작성해야함.

        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        //token은 access 아니면 refresh 2개뿐
        String tokenType = jwtUtil.getTokenType(token);
        if (tokenType.equals("refresh")) {
            chain.doFilter(request, response);   //refresh토큰이 있다 => /api/refresh/reissue는 인증 필요없는 곳 무사통과
            return;
        }


        //access token에 대해서....

        if (!jwtUtil.validateToken(token)) { //토큰이 문제 있다면.. jwtUtil에 문제가 없다면 만료되었을 때만.
            request.setAttribute("ERROR_CAUSE", "토큰만료");
            chain.doFilter(request, response);   // access_token이 만료된거라면 인증필요한 url => security가 authenticationException
            return;
        }

        //만료 안 되었다면 SecurityContext에 인증정보 담아 로그인한걸로 판단!!
        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            username); //내가 만든 CustomUserAccount
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);  //이걸 해야 비로소 securityConfig가 로그인한 걸로 간주
        chain.doFilter(request, response);  //인증된 상태로 통과!

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {  //띄어쓰기 주의
            return bearerToken.substring(7);
        }
        return null;
    }
}