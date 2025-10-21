package com.chat.stomp.interceptor;

import com.chat.security.JwtUtil;
import com.chat.security.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);  //매 새로운 메시지마다 생성됨.

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;  //CONNECT가 아니면 토큰검증 안함.
        }

        //CONNECT 요청에 대해 JWT 토큰 검증
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            // JWT가 없거나 잘못된 경우
            return null;  // 메시지 전송 차단 → 연결 거부
        }

        token = token.substring(7);
        String username = jwtUtil.validateAndExtractUsername(token);
        if (username == null) {
            return null; //검증실패
        }

        //정상적으로 검증된 경우, 사용자 인증 정보 설정
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        accessor.getSessionAttributes()
            .put("user", authenticationToken);  //연결할 때 session에 한번 저장 (HttpSession아님. 웹소켓세션)
        accessor.getSessionAttributes().put("roomId", accessor.getFirstNativeHeader(
            "roomId"));  //연결할 때 session에 한번 저장 (HttpSession아님. 웹소켓세션)   , 프론트에서 roomId 항상 온다고 가정.
        accessor.setUser(authenticationToken);
        return message;
    }

}
