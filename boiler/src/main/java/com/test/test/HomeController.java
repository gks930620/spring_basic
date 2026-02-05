package com.test.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/custom-oauth2/login/success")
    public String oauth2LoginSuccess() {
        return "oauth2-redirect";
    }

    @GetMapping("/community")
    public String community() {
        return "community/list";
    }

    @GetMapping("/community/write")
    public String communityWrite() {
        return "community/write";
    }

    @GetMapping("/community/detail")
    public String communityDetail() {
        return "community/detail";
    }

    @GetMapping("/community/edit")
    public String communityEdit() {
        return "community/edit";
    }
}
