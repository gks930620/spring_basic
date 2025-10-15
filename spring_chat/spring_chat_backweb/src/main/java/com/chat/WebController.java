package com.chat;

import com.chat.stomp.entity.RoomEntity;
import com.chat.stomp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final RoomRepository roomRepository;


    @GetMapping(value = {"/home" ,"/"})
    public String homePage() {
        return "home"; // templates/home.html
    }


    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage"; // templates/login.html
    }


    @GetMapping("/rooms")
    public String room목록() {
        return "rooms";
    }

    @GetMapping("/room/{roomId}")
    public String roomPage(@PathVariable("roomId") Long roomId, Model model) {
        // roomId를 model에 담으면 JS에서 필요 시 참조 가능
        model.addAttribute("roomId", roomId);
        return "room";  // room.html 반환
    }
}
