package com.shop.basic.article.repository;

import com.shop.basic.article.entity.Article;
import com.shop.basic.article.repository.dynamic.ArticleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
}