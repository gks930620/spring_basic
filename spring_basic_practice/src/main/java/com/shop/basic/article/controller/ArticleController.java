package com.shop.basic.article.controller;

import com.shop.basic.article.dto.ArticleAttachDTO;
import com.shop.basic.article.dto.request.ArticleCreateRequest;
import com.shop.basic.article.dto.request.ArticleUpdateRequest;
import com.shop.basic.article.dto.request.SearchArticleDTO;
import com.shop.basic.article.dto.response.ArticleDetailResponse;
import com.shop.basic.article.dto.response.ArticleListResponse;
import com.shop.basic.article.service.ArticleAttachService;
import com.shop.basic.article.service.ArticleService;
import com.shop.basic.common.PagedResponse;
import com.shop.basic.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    @Value("${file.upload.path}")
    private String uploadPath;
    private static  final String articlePath="article";

    private final ArticleService articleService;
    private  final ArticleAttachService attachService;


    @GetMapping("/article/new")
    public String newForm() {
        return "article/new-form"; // 뷰는 필요에 따라 Thymeleaf or React로
    }

    @PostMapping("/article")
    public String createArticle(ArticleCreateRequest articleDTO,
        @RequestParam(name = "files") List<MultipartFile> multipartFiles) throws IOException {
        List<ArticleAttachDTO> attachDTOS = FileUtil.saveFilesWith(multipartFiles,
            uploadPath+ File.separator+ articlePath+File.separator+ LocalDate.now(),
            meta -> new ArticleAttachDTO(null, meta.originalFileName(), meta.storedFileName(),
                meta.size(), meta.filePath(), meta.contentType()));

        articleService.saveArticle(articleDTO, attachDTOS);
        return "redirect:/articles";
    }

    @GetMapping("/articles")
    public String list( Model model,@ModelAttribute("search") SearchArticleDTO search
        , @RequestParam(defaultValue = "1") int page,  // 클라이언트 기준 1부터
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable= PageRequest.of( Math.max(0, page - 1),size);
        Page<ArticleListResponse> articles = articleService.findAllArticles(search,pageable);
        model.addAttribute("articles", new PagedResponse<>(articles)); //
        return "article/list"; // Thymeleaf로 보여줄 목록 화면
    }



    @GetMapping("/article/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        ArticleDetailResponse article = articleService.findById(id);
        model.addAttribute("article", article);
        return "article/view";
    }




    // 수정 form 이동
    @GetMapping("/article/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        ArticleDetailResponse article = articleService.findById(id);
        model.addAttribute("article", article);
        return "article/edit-form";
    }


    @PostMapping("/article/edit")
    public String updateArticle(ArticleUpdateRequest dto,
        @RequestParam("files") List<MultipartFile> files,
        @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds
    ) throws IOException {
        //삭제할 파일 삭제
        List<ArticleAttachDTO> deleteAttachDTOs = attachService.getAttachs(deleteFileIds);
        deleteAttachDTOs.forEach(attach -> FileUtil.deleteFile(attach.getFilePath(),attach.getStoredFileName()));
        // 파일생성
        List<ArticleAttachDTO> attachDTOS = FileUtil.saveFilesWith(files,
            uploadPath+ File.separator+ articlePath+File.separator+ LocalDate.now()
            , meta -> new ArticleAttachDTO(null, meta.originalFileName(), meta.storedFileName(),
                meta.size(), meta.filePath(), meta.contentType()));

        articleService.updateArticle(dto,deleteFileIds,attachDTOS);  //여기서  article 수정, attachDB에 추가 , 삭제 전부
        return "redirect:/article/" + dto.getId();
    }

    @PostMapping("/article/{id}/delete")
    public String deleteArticle(@PathVariable("id") Long id) {
        List<ArticleAttachDTO> deleteAttachDTOs = attachService.getAttachsByArticleId(id);
        deleteAttachDTOs.forEach(
            attach -> FileUtil.deleteFile(attach.getFilePath(), attach.getStoredFileName()));
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }




}


