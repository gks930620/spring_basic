package com.shop.basic.article.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleUpdateRequest {

    private Long id; // ✅ 수정 대상 게시글 id
    private String title;
    private String content;

}