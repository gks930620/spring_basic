package com.shop.basic.user.dto;

import com.shop.basic.user.entity.UserProfileImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileImageDTO {  //통합용.. 첨부파일DTO는 어디든 비슷
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
    private String filePath;
    private String contentType;


    public static UserProfileImageDTO fromEntity(UserProfileImage entity){
        UserProfileImageDTO dto= new UserProfileImageDTO();
        dto.setId(entity.getId());
        dto.setOriginalFileName(entity.getOriginalFileName());
        dto.setStoredFileName(entity.getStoredFileName());
        dto.setFileSize(entity.getFileSize());
        dto.setFilePath(entity.getFilePath());
        dto.setContentType(entity.getContentType());
        return dto;
    }
}