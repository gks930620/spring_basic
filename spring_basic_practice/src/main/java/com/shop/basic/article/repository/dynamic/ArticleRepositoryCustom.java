package com.shop.basic.article.repository.dynamic;

import com.shop.basic.article.dto.request.SearchArticleDTO;
import com.shop.basic.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {
    Page<Article> searchArticles(SearchArticleDTO search, Pageable pageable);
}