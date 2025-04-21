package com.shop.basic.user.entity;

import com.shop.basic.user.dto.request.UserJoinRequest;
import com.shop.basic.user.dto.request.UserUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(unique = true)
    private String username;  // 로그인 ID
    private String nickname;
    private String email;

    // 프로필 이미지 리스트
    @OneToMany(mappedBy = "user")
    private List<UserProfileImage> profileImages = new ArrayList<>();

    public void addProfileImage(UserProfileImage image) {
        profileImages.add(image);
        image.setUser(this);
    }


    public void update(UserUpdateRequest dto){
        this.setNickname(dto.getNickname());
        this.setEmail(dto.getEmail());
    }

    public static UserEntity fromJoinRequest(UserJoinRequest dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        return user;
    }



}