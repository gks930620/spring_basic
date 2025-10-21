package com.chat.stomp.config;

import com.chat.security.JwtUtil;
import com.chat.security.service.CustomUserDetailsService;
import com.chat.stomp.interceptor.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
       // WebSocketMessageBrokerConfigurer   이 인터페이스를 구현하며너 STOMP를 사용하게 되는 것.

    private  final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")  //spring 서버 내부의 웹소켓 엔드포인트
            .setAllowedOriginPatterns("*")
            // 모든 출처 허용 (CORS 설정) , 모두 허용 ; localhost:8080 외의 url에서도   이 websocket서버 엔드포인트에 연결요청가능함.
            // 실무에서는 내 서버 url만 허용하던가 특정 url만 허용하도록 설정해야 함.
            .withSockJS(); // SockJS fallback 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트 → 서버
        // pub/room/1 이면 room/1 채팅방(구독한 사람들)에  메시지 전달

        registry.enableSimpleBroker("/sub"); // 서버 → 클라이언트 (구독)
        // sub/room/1   구독하면  사용자는 room/1  채팅방에 있는 것
    }

    // 채널 인터셉터 등록,  인터셉터를 적용해서
    // 모든  채팅 점검,   이 인터셉터에서  CONNECT 요청에 대해 JWT 토큰 검증 수행
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtUtil, customUserDetailsService));
    }
}
