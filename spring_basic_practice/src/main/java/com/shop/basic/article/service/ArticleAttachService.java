package com.shop.basic.article.service;

import com.shop.basic.article.dto.ArticleAttachDTO;
import com.shop.basic.article.entity.Article;
import com.shop.basic.article.entity.ArticleAttach;
import com.shop.basic.article.repository.ArticleAttachRepository;
import com.shop.basic.article.repository.ArticleRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleAttachService {

    private final ArticleAttachRepository attachRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public List<ArticleAttachDTO> getAttachs(List<Long> deleteAttachIds) {
        if (deleteAttachIds != null && !deleteAttachIds.isEmpty()) {
            List<ArticleAttach> result = attachRepository.findAllByIdIn(deleteAttachIds);
            return result.stream().map(ArticleAttachDTO::fromEntity).toList();
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<ArticleAttachDTO> getAttachsByArticleId(Long articleId) {
        Article proxyArticle = articleRepository.getReferenceById(articleId);
        List<ArticleAttach> result = attachRepository.findByArticle(proxyArticle);
        return result.stream().map(ArticleAttachDTO::fromEntity).toList();
    }


}
