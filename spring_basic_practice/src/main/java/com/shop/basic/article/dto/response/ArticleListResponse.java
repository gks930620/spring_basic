package com.shop.basic.article.dto.response;


import com.shop.basic.article.entity.Article;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleListResponse {

    private Long id;
    private String title;

    public ArticleListResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static ArticleListResponse fromEntity(Article entity){
        return new ArticleListResponse(entity.getId(), entity.getTitle()) ;
    }


}