package com.chat;

import com.chat.stomp.entity.RoomEntity;
import com.chat.stomp.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomDataInitializer {

    private final RoomRepository roomRepository;

    @PostConstruct
    public void initRooms() {
        if (roomRepository.count() > 0) {
            return;
        }

        roomRepository.save(new RoomEntity(null, "자유 채팅방"));
        roomRepository.save(new RoomEntity(null, "개발자 채팅방"));
        roomRepository.save(new RoomEntity(null, "스터디 모임"));
        System.out.println("✅ 기본 채팅방 3개 생성 완료");
    }
}
