package com.shop.basic.article.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchArticleDTO {

    private String searchType;
    private String searchWord;

}
