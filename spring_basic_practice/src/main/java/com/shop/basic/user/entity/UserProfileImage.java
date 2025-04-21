package com.shop.basic.user.entity;

import com.shop.basic.user.dto.UserProfileImageDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
    private String filePath;
    private String contentType;


    public static UserProfileImage fromDTO(UserProfileImageDTO dto) {
        UserProfileImage entity = new UserProfileImage();
        entity.setOriginalFileName(dto.getOriginalFileName());
        entity.setStoredFileName(dto.getStoredFileName());
        entity.setFileSize(dto.getFileSize());
        entity.setFilePath(dto.getFilePath());
        entity.setContentType(dto.getContentType());
        return entity;
    }


}