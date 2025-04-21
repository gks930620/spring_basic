package com.shop.basic.article.repository.dynamic;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.basic.article.dto.request.SearchArticleDTO;
import com.shop.basic.article.entity.Article;
import com.shop.basic.article.entity.QArticle;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Article> searchArticles(SearchArticleDTO search, Pageable pageable) {
        QArticle article = QArticle.article;


        List<Article> results = queryFactory
            .selectFrom(article)
            .where(getSearchCondition(search))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(article.id.desc())
            .fetch();

        Long total = queryFactory
            .select(article.count())
            .from(article)
            .where(getSearchCondition(search))
            .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    private BooleanExpression getSearchCondition(SearchArticleDTO search) {
        if (search == null || isBlank(search.getSearchWord())) return null;

        String type = search.getSearchType();
        String word = search.getSearchWord();

        return switch (type) {
            case "title" -> titleContains(word);
            case "content" -> contentContains(word);
            default -> null;
        };
    }

    private BooleanExpression titleContains(String word) {
        return isBlank(word) ? null : QArticle.article.title.containsIgnoreCase(word);
    }

    private BooleanExpression contentContains(String word) {
        return isBlank(word) ? null : QArticle.article.content.containsIgnoreCase(word);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}