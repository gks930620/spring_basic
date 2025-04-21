package com.shop.basic.article.dto.response;

import com.shop.basic.article.dto.ArticleAttachDTO;
import com.shop.basic.article.entity.Article;
import com.shop.basic.article.entity.ArticleAttach;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ArticleDetailResponse {

    private Long id;
    private String title;
    private String content;
    private List<ArticleAttachDTO> attaches = new ArrayList<>();


    public static ArticleDetailResponse fromEntity(Article article, List<ArticleAttach> attaches) {
        ArticleDetailResponse dto = new ArticleDetailResponse();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        if(attaches!=null){
            List<ArticleAttachDTO> attachDTOS =  attaches.stream().map(ArticleAttachDTO::fromEntity).toList();
            dto.setAttaches(attachDTOS);
        }
        return dto;
    }
    //만약 패치조인등을 한다면  Article만 파라미터로 받는 fromEntity도 만들 수 있겠지.
    public static ArticleDetailResponse fromEntity(Article article){
        return fromEntity(article,article.getAttaches());
    }


}