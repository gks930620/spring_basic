package com.shop.basic.user.dto.response;

import com.shop.basic.user.dto.UserProfileImageDTO;
import com.shop.basic.user.entity.UserEntity;
import com.shop.basic.user.entity.UserProfileImage;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;

    private String username;  // 로그인 ID
    private String nickname;
    private String email;

    List<UserProfileImageDTO> profileImages=new ArrayList<>();


    public static UserInfoResponse fromEntity(UserEntity entity, List<UserProfileImage> entityProfileImages){
        UserInfoResponse dto=new UserInfoResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setNickname(entity.getNickname());
        dto.setEmail(entity.getEmail());
        dto.setProfileImages(entityProfileImages.stream().map(UserProfileImageDTO::fromEntity).toList() );
        return dto;
    }

}
