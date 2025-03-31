package com.example.security.controller;

import com.example.security.model.JoinDTO;
import com.example.security.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/join")
@RequiredArgsConstructor
public class JoinController {

    private  final JoinService joinService;

    @GetMapping("/join")
    public  String join(){
        return "join";
    }

    @ResponseBody
    @PostMapping("/join")
    public  String joinPost(JoinDTO joinDTO){
        joinService.joinProcess(joinDTO);
        return "회원가입 완료!";
    }
}
