package com.chat.stomp.config;

import com.chat.stomp.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;


    //연결됐을 때 실행
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

            System.out.println("roomId 따로 안보냈을 텐데 " + roomId);

            ChatMessage enterMsg = new ChatMessage(null,username + "님이 입장했습니다.");

            messagingTemplate.convertAndSend("/sub/room/" + roomId, enterMsg);
            //여기에서 해당 방에만..
            // html에   stompClient.subscribe(`/sub/room/${roomId}`  주소랑 같아야함.)
            // 실제 방을 나누는건 html에 stocmClient.subscribe를 통해   Stomp websocket이 방 나누는거고
            // 내 코드는 그 방에 메세지 전달하는 것 뿐
        }
    }
}
