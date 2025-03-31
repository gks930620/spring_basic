package com.example.security.controller;


import com.example.security.model.CustomUserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    //로그인 후
    @RequestMapping("/my/info")
    @ResponseBody
    public String  myInfo(@AuthenticationPrincipal CustomUserAccount userDetails
        //개발자가 직접 만든 로그인 정보. 이거 사용하세요!
        ){
        StringBuilder sb=new StringBuilder();
        sb.append("username : " +  userDetails.getUsername()  +"<br>");
        sb.append("nickname : " +  userDetails.getNickname()  +"<br>");
        sb.append("email : " +  userDetails.getEmail()  +"<br>");
        sb.append(  "권한 : "+    userDetails.getAuthorities().iterator().next().getAuthority() +"<br>"   );
        return sb.toString();
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String  admin(){
        return "권한이없어서 여기에 도달 할 수 없다.";
    }
}
