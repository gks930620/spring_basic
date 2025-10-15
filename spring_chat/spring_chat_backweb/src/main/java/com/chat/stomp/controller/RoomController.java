package com.chat.stomp.controller;

import com.chat.stomp.entity.RoomEntity;
import com.chat.stomp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    // 전체 방 목록
    @GetMapping("/rooms")
    public List<RoomEntity> getRooms() {
        return roomRepository.findAll();
    }

    // 특정 방 상세
    @GetMapping("/room/{roomId}")
    public RoomEntity getRoom(@PathVariable Long roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 방입니다."));
    }

}
