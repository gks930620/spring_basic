package com.example.security.service;

import com.example.security.entity.UserEntity;
import com.example.security.model.CustomUserAccount;
import com.example.security.model.OAuthProvider;
import com.example.security.repository.UserRepository;
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
        OAuth2User oAuth2User = super.loadUser(userRequest);//  user-info-uri 에서 유저정보를 가져옴.
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); //"google","kakao"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthProvider currentLoginProvider=OAuthProvider.from(registrationId); //GOOGLE,KAKAO   ENUM
        UserEntity userEntity = currentLoginProvider.toUserEntity(attributes);  //oauth2사용자의 user정보
        if(userRepository.findByUsername(userEntity.getUsername()) ==null){  //처음로그인
            userRepository.save(userEntity);  //유저정보 저장
        }

        return new CustomUserAccount(userEntity, attributes);

    }
}

