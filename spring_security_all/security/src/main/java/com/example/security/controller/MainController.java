package com.example.security.controller;


import com.example.security.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    //로그인 후
    @RequestMapping("/my/info")
    @ResponseBody
    public String  myInfo(@AuthenticationPrincipal CustomUserDetails userDetails,  //개발자가 직접 만든 로그인 정보. 이거 사용하세요!
        Principal principal, //  username만 가져올 수 있음.
        Authentication authentication,    //spring securiy객체가 실제 사용하는 로그인정보.  userDetails를 포함 + 좀 더 다양한정보
        HttpSession session,
        HttpServletRequest request
        ){
        StringBuilder sb=new StringBuilder();
        sb.append(  "권한 : "+    userDetails.getAuthorities().iterator().next().getAuthority() +"<br>"   );  //첫번째권한.
        sb.append(  "password : "+  userDetails.getPassword()  +"<br> ");
        sb.append(  "username : "+  userDetails.getUsername() + "<br>" );
        //DB에 있는 정보들을 더 원한다면 CusomuserDetails에서 getUserEntity() 메소드를 만들거나 하자.

        //SecurityContextHolder.getContext().getAuthentication();   위의 Authentcaton과 동일
        //request.getUserPrincipal()    위의 Principal과 동일
         //session.getAttribute("SPRING_SECURITY_CONTEXT").  SecurityContextHolder.getContext()랑 동일


        return sb.toString();
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String  admin(){
        return "따로 admin권한 안해놔서 회원가입 로그인해봤자 여기에 도달 할 수 없다. 403화면보게 될걸";
    }
}
