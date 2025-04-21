package com.shop.basic.article.dto;

import com.shop.basic.article.entity.ArticleAttach;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleAttachDTO {  //통합용.. 첨부파일용DTO는 어디든 내용 비슷
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
    private String filePath;
    private String contentType;

    public static ArticleAttachDTO fromEntity(ArticleAttach attach) {
        return new ArticleAttachDTO(
            attach.getId(),
            attach.getOriginalFileName(),
            attach.getStoredFileName(),
            attach.getFileSize(),
            attach.getFilePath(),
            attach.getContentType()
        );
    }
}
