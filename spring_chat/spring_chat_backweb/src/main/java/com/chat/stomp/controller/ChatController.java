package com.chat.stomp.controller;

import com.chat.security.model.CustomUserAccount;
import com.chat.stomp.model.ChatMessage;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    
      
    // 클라이언트 -> 서버
    @MessageMapping("/room/{roomId}")   //pub일 때, send일 때만 옴
    public void sendMessage(
        @DestinationVariable String roomId,
        ChatMessage message,
        Message<?> msg
        ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(msg);  //매 새로운 메시지마다 생성됨.
        Object sessionUser = accessor.getSessionAttributes().get("user");
        Authentication auth=(Authentication) sessionUser;
        String username = auth.getName();
        message.setSender(username);
        messagingTemplate.convertAndSend("/sub/room/" + roomId, message);  //여기에서 해당 방에만..
    }
}
