package com.security.jwt.service;

import com.security.jwt.entity.UserEntity;
import com.security.jwt.model.CustomUserAccount;
import com.security.jwt.model.OAuthProvider;
import com.security.jwt.repository.UserRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;


    @Transactional(readOnly = false)
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 카카오로그인이든 폼로그인이든 똑같은 서비스를 제공하기 위해 DB 저장

        OAuth2User oAuth2User = super.loadUser(userRequest);//  user-info-uri : https://kapi.kakao.com/v2/user/me 에서 유저정보를 가져옴.

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //"kakao"



        // OAuth2 제공자(Kakao)에서 가져온 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();
        /**
         {
         "id": 1234567890,  문자아님 Long
         "kakao_account": {
         "email": "user@example.com",
         "profile": {
         "nickname": "홍길동"
         }
         }
         }
         */
        Long id= (Long) attributes.get("id");  //카카오는 id가 Long.
        Map<String,Object> kakaoAccount=(Map<String,Object>)attributes.get("kakao_account");
        String email= (String) kakaoAccount.get("email");
        Map<String,Object> profile=(Map<String,Object>)kakaoAccount.get("profile");
        String  nickname=(String)profile.get("nickname");



        //  DB에서 사용자 조회 (없으면 생성)
        UserEntity user = userRepository.findByUsername("kakao"+id);
        if (user == null) {  //카카오로 처음 로그인하는 분
            user = new UserEntity();
            user.setUsername("kakao"+id);
            user.setPassword("{noop}oauth2user"); // OAuth2 로그인은 비밀번호 없음. {noop}은 security가  암호화 안된 비밀번호임을 암시.
            user.setEmail(email);
            user.setNickname(nickname);
            user.getRoles().add("USER");

            user.setProvider(registrationId);
            userRepository.save(user);
        }else{  //처음 로그인은 아님. 카카오에서 nickname변경됐다면 우리 DB에도  반영해야지
            //username password는 안 바뀜.
            user.setEmail(email);
            user.setNickname(nickname);
        }

        //CustomUserAccount로 반환 (OAuth2User + UserDetails 통합)
        return new CustomUserAccount(user, attributes);
    }
}