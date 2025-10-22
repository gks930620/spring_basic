package com.chat.stomp.config;

import com.chat.stomp.model.ChatMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;


    //연결됐을 때만 한번 실행
    //순서상 JwtChannelInterceptor  => 실제 연결 =>  여기.
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Object sessionUser = accessor.getSessionAttributes().get("user");
        Authentication auth= (Authentication) sessionUser;

        Object sessionRoomId = accessor.getSessionAttributes().get("roomId");
        String roomId=(String)sessionRoomId;

        if (auth != null) {
            String username = auth.getName();
            // roomId는 클라이언트에서 헤더로 보내거나, 세션에서 관리
            messagingTemplate.convertAndSend("/sub/room/" + roomId, username + "님이 입장했습니다.");

            //여기에서 해당 방에만..
            // html에   stompClient.subscribe(`/sub/room/${roomId}`  주소랑 같아야함.)
            // 실제 방을 나누는건 html에 stocmClient.subscribe를 통해   Stomp websocket이 방 나누는거고
            // 내 코드는 그 방에 메세지 전달하는 것 뿐

            //ChatController는 sendMessage에서 메시지 보낼 때마다 실행되지만  연결될 때는 실행X
            //여기는 연결될 때 한번만 실행됨. 한번 실행할 때 입장메세지 보내는 기능일 뿐.

        }
    }


    // 연결 해제(퇴장)   뒤로가기, 브라우저 종료 등 다 눈치챔.  앱에서 뒤로가기 등도 다 감지함.
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) accessor.getSessionAttributes().get("user");
        String roomId = (String) accessor.getSessionAttributes().get("roomId");

        if (auth != null && roomId != null) {
            String username = auth.getName();
            messagingTemplate.convertAndSend("/sub/room/" + roomId, username + "님이 퇴장했습니다.");
        }
    }
}
