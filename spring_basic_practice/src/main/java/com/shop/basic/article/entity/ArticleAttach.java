package com.shop.basic.article.entity;

import com.shop.basic.article.dto.ArticleAttachDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ArticleAttach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;
    private String originalFileName;
    private String storedFileName;   //고유한 이름.
    private Long fileSize;
    private String filePath;
    private String contentType;    // MIME타입 (image/png, application/pdf 등)


    public ArticleAttach(Article article, String originalFileName, String storedFileName,
        Long fileSize,
        String filePath, String contentType) {
        this.article = article;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.contentType = contentType;
    }

    public static ArticleAttach fromDTO(ArticleAttachDTO dto) {
        ArticleAttach attach = new ArticleAttach();
        attach.setOriginalFileName(dto.getOriginalFileName());
        attach.setStoredFileName(dto.getStoredFileName());
        attach.setFileSize(dto.getFileSize());
        attach.setContentType(dto.getContentType());
        attach.setFilePath(dto.getFilePath());
        return attach;
    }
}
