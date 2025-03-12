package com.security.jwt.filter;

import com.security.jwt.JwtUtil;
import com.security.jwt.model.CustomUserAccount;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
        if (token == null) {
            //토큰 없을 떄는 그냥 다음필터로
            //인증이 필요없는곳 => 무사통과
            //인증이  필요한곳  =>  securityConfig의 authentication에 걸림 => config의  authenticationEntryPoint 에서 처리
            chain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.validateToken(token)) { //토큰이 문제 있다면.. 보통 만료돼었을 때
            request.setAttribute("ERROR_CAUSE", "토큰만료");
            chain.doFilter(request,response);   //로그인을 안한거기 때문에 역시 seucirtyConfig의 authetncation에 걸림.
            return;
        }
        String username = jwtUtil.extractUsername(token);

        //토큰도 있고, 토큰도 문제없음.   securityContext에 담아 로그인한걸로 판단!!.
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            username); //내가 만든 CustomUserAccount
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {  //띄어쓰기 주의
            return bearerToken.substring(7);
        }
        return null;
    }
}