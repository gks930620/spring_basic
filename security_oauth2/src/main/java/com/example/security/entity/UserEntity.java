package com.example.security.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   //JPA 기본 ID Long 


    @Column(unique = true)
    private String username;   //spring이 entity의 필드랑 파라미터를 비교하는건 아님.   security에서 username이 기본이지만, 사용자입장에선 ID
    private String password;  //spring이  entity의 필드랑 파라미터를 비교하는건 아님

    private String email;
    private String nickname;

    private String provider;  // Oauth2Provider 이름.


    private List<String> roles=new ArrayList<>();

}
