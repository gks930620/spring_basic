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


    //private String name;   //진짜 유저 이름. 홍길동


    private List<String> roles=new ArrayList<>();
    //권한은 기본적으로 여러개입니다. 많은 곳에서 권한을 간단히 하려고 String 1개만 하는 경우가 대부분이지만
    // Security도  권한 여러개를 기본으로 제공하기 때문에 여러개인게 더 편함.


}
