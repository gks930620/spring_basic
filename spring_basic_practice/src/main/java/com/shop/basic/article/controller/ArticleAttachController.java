package com.shop.basic.article.controller;

import com.shop.basic.article.entity.ArticleAttach;
import com.shop.basic.article.repository.ArticleAttachRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class ArticleAttachController {
    private final ArticleAttachRepository attachRepository;


    @ResponseBody  //NOTE : ResponseEntity는  ResponoseBody를 붙이도록
    @GetMapping("/files/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) throws IOException {
        ArticleAttach attach = attachRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("첨부파일이 존재하지 않습니다."));

        // 저장된 파일 경로
        Path filePath = Paths.get(attach.getFilePath(), attach.getStoredFileName());
        Resource resource = new InputStreamResource(Files.newInputStream(filePath));

        // 다운로드 시 보여줄 파일 이름 (인코딩)
        String encodedFileName = URLEncoder.encode(attach.getOriginalFileName(), StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
            .header(HttpHeaders.CONTENT_TYPE, attach.getContentType())
            .body(resource);

    }


}
