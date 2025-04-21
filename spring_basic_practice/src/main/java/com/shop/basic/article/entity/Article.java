package com.shop.basic.article.entity;


import com.shop.basic.article.dto.request.ArticleCreateRequest;
import com.shop.basic.article.dto.request.ArticleUpdateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    public Article(String content, String title) {
        this.content = content;
        this.title = title;
    }

    @OneToMany(mappedBy = "article")
    private List<ArticleAttach> attaches = new ArrayList<>();

    public void addAttachment(ArticleAttach attach) {
        attaches.add(attach);
        attach.setArticle(this);
    }


    public void update(ArticleUpdateRequest dto) {
        this.setTitle(dto.getTitle());
        this.setContent(dto.getContent());
    }
    public static Article fromCreateRequest(ArticleCreateRequest dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        return article;
    }

}
