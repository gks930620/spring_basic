package com.example.security.model;

import com.example.security.entity.UserEntity;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.userdetails.User;

public enum OAuthProvider {
    GOOGLE("google") {
        @Override
        public UserEntity toUserEntity(Map<String, Object> attributes) {
            UserEntity user = new UserEntity();
            user.setProvider(this.getRegistrationId());
            user.setUsername(this.getRegistrationId()+(String) attributes.get("sub"));  //google15432323
            user.setPassword("{noop}oauth2user");
            user.setEmail((String) attributes.get("email"));
            user.setNickname((String) attributes.get("name"));
            user.getRoles().add("USER");
            return user;
        }
    },
    KAKAO("kakao") {
        @Override
        public UserEntity toUserEntity(Map<String, Object> attributes) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

            Long id= (Long) attributes.get("id");  //카카오는 id가 Long.

            UserEntity user = new UserEntity();
            user.setProvider(this.getRegistrationId());  //kakao
            user.setUsername(this.getRegistrationId()+id);  //kokao15432323
            user.setPassword("{noop}oauth2user");
            user.setEmail((String) kakaoAccount.get("email"));
            user.setNickname((String) properties.get("nickname"));
            user.getRoles().add("USER");
            return user;
        }
    };



    private final String registrationId;
    OAuthProvider(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public static OAuthProvider from(String registrationId) {
        for (OAuthProvider provider : OAuthProvider.values()) {
            if (provider.getRegistrationId().equalsIgnoreCase(registrationId)) { //대소문자구분없이.
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + registrationId);
    }
    public abstract UserEntity toUserEntity(Map<String, Object> attributes);

}


/**
 구글
{
    "sub": "112233445566778899001",
    "name": "John Doe",
    "given_name": "John",
    "family_name": "Doe",
    "picture": "https://lh3.googleusercontent.com/a-/AOh14Gh...",
    "email": "johndoe@gmail.com",
    "email_verified": true,
    "locale": "en"
}


 카카오
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




