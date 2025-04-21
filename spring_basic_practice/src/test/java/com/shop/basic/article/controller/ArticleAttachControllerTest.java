package com.shop.basic.article.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shop.basic.article.entity.ArticleAttach;
import com.shop.basic.article.repository.ArticleAttachRepository;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

;


@ActiveProfiles("test")   //application-test.yml 설정으로 서버킴.
@SpringBootTest// 전체 애플리케이션 컨텍스트생성. 서버가 켜지고 서버설정(빈, configuration) 등이 똑같이 적용됨.
@AutoConfigureMockMvc //mockMvc자동설정해줌.
@Transactional  //Test후 DB변경사항 롤백.   위치는 클래스.
class ArticleAttachControllerTest {
    @Autowired
    private ArticleAttachRepository attachRepository;
    @Autowired
    private MockMvc mockMvc;
    @Value("${file.upload.path}")   //테스트용 application.yml을 따로 만들지 않으면 실제 폴더가 삭제됨/
    private String uploadPath;
    private static final String articlePath = "article";
    @AfterEach
    void fileDelete() throws IOException {
        // 테스트 끝나면 디렉토리 통째로 삭제
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();
        Path root = Path.of(todayPath);
        if (Files.exists(root)) {
            FileSystemUtils.deleteRecursively(root);
        }
    }

    @DisplayName("/files/{id}/download,GET : 파일 다운로드에 성공한다.")
    @Test
    void 파일다운로드_성공()throws Exception{
        //given
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();
        ArticleAttach attach = new ArticleAttach();
        attach.setFileSize(100L);
        attach.setContentType("text/plain");
        attach.setFilePath(todayPath);
        attach.setOriginalFileName("원래파일이름.txt");
        attach.setStoredFileName(UUID.randomUUID()+"-" + attach.getOriginalFileName());
        ArticleAttach savedAttach = attachRepository.save(attach);
        //파일
        File folder = new File(savedAttach.getFilePath());
        if (!folder.exists()) folder.mkdirs();
        File actualFile = new File(savedAttach.getFilePath(), savedAttach.getStoredFileName());
        Files.writeString(actualFile.toPath(), "기존 파일 내용",StandardCharsets.UTF_8);

        //when
        mockMvc.perform(get("/files/" + savedAttach.getId() + "/download"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + URLEncoder.encode(savedAttach.getOriginalFileName(),
                    StandardCharsets.UTF_8).replaceAll("\\+", "%20") + "\""));
        //파일다운로드는 then 없이 when에서 header가 Content-Disposition: attachment; filename="example.txt" 이냐 물어봄
        //aftereach에서 자동으로 생성된파일 삭제합니다.
    }
}