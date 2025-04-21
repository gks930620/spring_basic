package com.shop.basic.article.service;

import com.shop.basic.article.dto.ArticleAttachDTO;
import com.shop.basic.article.dto.request.ArticleCreateRequest;
import com.shop.basic.article.dto.request.SearchArticleDTO;
import com.shop.basic.article.dto.response.ArticleDetailResponse;
import com.shop.basic.article.dto.response.ArticleListResponse;
import com.shop.basic.article.dto.request.ArticleUpdateRequest;
import com.shop.basic.article.entity.Article;
import com.shop.basic.article.entity.ArticleAttach;
import com.shop.basic.article.repository.ArticleAttachRepository;
import com.shop.basic.article.repository.ArticleRepository;
import com.shop.basic.util.FileUtil;
import jakarta.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleAttachRepository attachRepository;

    @Transactional(readOnly = false)
    public void saveArticle(ArticleCreateRequest dto,  List<ArticleAttachDTO> attachDTOS )    {
        Article article=Article.fromCreateRequest(dto);  //article + article Attaches까지
        articleRepository.save(article);
        attachRepository.saveAll(
            attachDTOS.stream().map(ArticleAttach::fromDTO)
                .peek( attach -> attach.setArticle(article)).toList());
    }

    @Transactional
    public void updateArticle(ArticleUpdateRequest dto, List<Long> deleteFileIds, List<ArticleAttachDTO> attachDTOS){
        Article article= articleRepository.findById(dto.getId()).orElseThrow(EntityNotFoundException::new);


        if( deleteFileIds!=null && ! deleteFileIds.isEmpty()){
            attachRepository.deleteAllById(deleteFileIds); // 삭 제할 Attache들 삭제
        }
        article.update(dto);   //내용수정하고
        attachRepository.saveAll(   attachDTOS.stream().map(ArticleAttach::fromDTO).
            peek( attach -> attach.setArticle(article)).toList() );  //추가된 파일들 saveAll
    }

    public Page<ArticleListResponse> findAllArticles(SearchArticleDTO dto, Pageable pageable) {
        Page<Article> articles = articleRepository.searchArticles(dto, pageable);
        return articles.map(ArticleListResponse::fromEntity);
    }


    public ArticleDetailResponse findById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<ArticleAttach> attaches= attachRepository.findByArticle(article);
        return ArticleDetailResponse.fromEntity(article,attaches);
    }



    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
        // 첨부파일 먼저 조회
        List<ArticleAttach> attaches = attachRepository.findByArticle(article);
        // DB에서 첨부파일 삭제
        attachRepository.deleteAll(attaches);
        // 게시글 삭제
        articleRepository.delete(article);
    }


}
