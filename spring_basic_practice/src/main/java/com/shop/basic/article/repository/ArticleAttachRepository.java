package com.shop.basic.article.repository;

import com.shop.basic.article.entity.Article;
import com.shop.basic.article.entity.ArticleAttach;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleAttachRepository extends JpaRepository<ArticleAttach, Long> {

    public List<ArticleAttach> findByArticle(Article article);

    public List<ArticleAttach> findAllByIdIn(List<Long> ids);

}