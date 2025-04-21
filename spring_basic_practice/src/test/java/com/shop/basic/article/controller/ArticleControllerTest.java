package com.shop.basic.article.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shop.basic.article.dto.response.ArticleDetailResponse;
import com.shop.basic.article.dto.response.ArticleListResponse;
import com.shop.basic.article.entity.Article;
import com.shop.basic.article.entity.ArticleAttach;
import com.shop.basic.article.repository.ArticleAttachRepository;
import com.shop.basic.article.repository.ArticleRepository;
import com.shop.basic.common.PagedResponse;
import jakarta.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileSystemUtils;


@ActiveProfiles("test")   //application-test.yml 설정으로 서버킴.
@SpringBootTest// 전체 애플리케이션 컨텍스트생성. 서버가 켜지고 서버설정(빈, configuration) 등이 똑같이 적용됨.
@AutoConfigureMockMvc //mockMvc자동설정해줌.
@Transactional  //Test후 DB변경사항 롤백.   위치는 클래스.
class ArticleControllerTest {

    /***
     * 1API = 1테스트메소드 X
     * 1 테스트목적 = 1 테스트메소드
     */

    @Value("${file.upload.path}")   //테스트용 application.yml을 따로 만들지 않으면 실제 폴더가 삭제됨/
    private String uploadPath;

    @Autowired
    private MockMvc mockMvc;    // 컨트롤러 테스트용 HTTP 호출 도구

    private static final String articlePath = "article";


    @Autowired
    private ArticleRepository articleRepository;  //내가 임의로 글 저장한 상태만들기위한용도.
    @Autowired
    private ArticleAttachRepository attachRepository;

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


    @DisplayName("/article, POST : 새로운 article을 생성 시 파일들이 잘 저장돠는지 확인한다.")
    @Test
    void createArticle_shouldSaveFilesToDisk() throws Exception {
        // 준비: 파일 2개 + title/content
        // given - MockMultipartFile로 요청 데이터 준비
        MockMultipartFile file1 = new MockMultipartFile(
            "files", "test1.txt", "text/plain", "File1 contents".getBytes());

        MockMultipartFile file2 = new MockMultipartFile(
            "files", "test2.txt", "text/plain", "File2 contents".getBytes());

        // when - multipart 요청 전송
        mockMvc.perform(multipart("/article")
                .file(file1)
                .file(file2)
                .param("title", "테스트 제목")
                .param("content", "테스트 내용")
                .characterEncoding("UTF-8"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/articles"));

        // 검증: 오늘 날짜 폴더 안에 파일이 2개 저장되어야 함     title,content가 있지만 여기서는 파일만 검증
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();
        File folder = new File(todayPath);
        assertThat(folder.exists()).isTrue();
        assertThat(folder.isDirectory()).isTrue();

        File[] files = folder.listFiles();
        assertThat(files).isNotNull();
        assertThat(files.length).isEqualTo(2);
    }

    @DisplayName("/article, POST : 게시글과 첨부파일이 DB에 저장된다.")
    @Test
    void createArticle_savesToDatabase() throws Exception {
        // given - MockMultipartFile로 요청 데이터 준비
        MockMultipartFile file1 = new MockMultipartFile(
            "files", "test1.txt", "text/plain", "File1 contents".getBytes());

        MockMultipartFile file2 = new MockMultipartFile(
            "files", "test2.txt", "text/plain", "File2 contents".getBytes());

        // when - multipart 요청 전송
        mockMvc.perform(multipart("/article")
                .file(file1)
                .file(file2)
                .param("title", "테스트 제목")
                .param("content", "테스트 내용")
                .characterEncoding("UTF-8"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/articles"));

        // then - 게시글 저장 확인
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).hasSize(1);

        Article savedArticle = articles.get(0);
        assertThat(savedArticle.getTitle()).isEqualTo("테스트 제목");
        assertThat(savedArticle.getContent()).isEqualTo("테스트 내용");

        //게시글의 첨부파일내용 DB저장확인.
        List<ArticleAttach> attaches = attachRepository.findByArticle(savedArticle);
        assertThat(attaches).hasSize(2);
        assertThat(attaches.get(0).getArticle().getId()).isEqualTo(savedArticle.getId());
        assertThat(attaches.get(0).getOriginalFileName()).isEqualTo("test1.txt");
        assertThat(attaches.get(1).getOriginalFileName()).isEqualTo("test2.txt");
    }


    @DisplayName("/articles : article목록을 조회한다.")
    @Test
    void 게시글_목록_조회_성공() throws Exception {
        //given
        Article article = new Article();
        article.setTitle("테스트 제목");
        article.setContent("테스트 내용");
        Article saveArticle = articleRepository.save(article);

        // when: /articles 호출
        MvcResult result = mockMvc.perform(get("/articles")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("articles"))
            .andReturn();

        // then: model 에서 PagedResponse 꺼내 검증
        ModelMap modelMap = result.getModelAndView().getModelMap();
        PagedResponse<ArticleListResponse> response =
            (PagedResponse<ArticleListResponse>) modelMap.get("articles");
        assertThat(response.getContent()).isNotEmpty();
        assertThat(response.getContent().get(0).getTitle()).isEqualTo(
            saveArticle.getTitle());  //꺼낸거 아까 저장한 DB의 내용과같은지.
    }


    @DisplayName("/article/{id},GET : 게시글 1개의 정보를 확인한다. ")
    @Test
    void 게시글_상세_조회_성공() throws Exception {
        //given
        Article article = new Article();
        article.setTitle("테스트 제목");
        article.setContent("테스트 내용");
        Article saveArticle = articleRepository.save(article);

        ArticleAttach attach = new ArticleAttach();
        attach.setArticle(saveArticle);
        attach.setOriginalFileName("File1");
        attach.setFileSize(100L);
        ArticleAttach attach2 = new ArticleAttach();
        attach2.setArticle(saveArticle);
        attach2.setOriginalFileName("File2");
        attach2.setFileSize(100L);
        attachRepository.save(attach);
        attachRepository.save(attach2);

        //when
        MvcResult result = mockMvc.perform(get("/article/" + saveArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("article"))
            .andReturn();

        //then : 모델에서 article 추출
        ModelMap modelMap = result.getModelAndView().getModelMap();
        ArticleDetailResponse response =
            (ArticleDetailResponse) modelMap.get("article");

        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");  //Article 내용 확인
        assertThat(response.getId()).isEqualTo(saveArticle.getId());
        assertThat(response.getAttaches()).hasSize(2);
        assertThat(response.getAttaches().get(0).getOriginalFileName()).isEqualTo(
            attach.getOriginalFileName()); //Article과 파일정보들 확인

    }



    //edit form은  view랑 똑같은로직이라 생략.

    @DisplayName("/article/{id}/edit , POST : 게시글을 업데이드 할 때 삭제하는 파일들이 잘 삭제되는지 확인")
    @Test
    void 게시글_수정_파일삭제_성공() throws Exception {
        // 저장된 게시글
        Article savedArticle = articleRepository.save(new Article("기존제목", "기존내용"));
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();

        //파일과 파일DB  (기존에 있던 파일들.)
        String originFileName="삭제파일1.txt";
        String storedName = UUID.randomUUID() + "_" +originFileName;

        File folder = new File(todayPath);
        if (!folder.exists()) folder.mkdirs();
        File actualFile = new File(todayPath, storedName);
        Files.writeString(actualFile.toPath(), "기존 파일 내용", StandardCharsets.UTF_8);

        ArticleAttach attach1 = new ArticleAttach(
            savedArticle,originFileName,storedName,100L,todayPath,"text/plain"); // DB에 파일저장정보 직접 저장
        ArticleAttach saveAttach1 = attachRepository.save(attach1);

        // 수정용 파일
        MockMultipartFile newFile = new MockMultipartFile("files", "newFile.txt", "text/plain",
            "새파일".getBytes());

        //when
        mockMvc.perform(multipart("/article/edit")
                .file(newFile)
                .param("id", savedArticle.getId().toString())
                .param("title", "수정된 제목")
                .param("content", "수정된 내용")
                .param("deleteFileIds", saveAttach1.getId().toString())
                .characterEncoding("UTF-8"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/article/" + savedArticle.getId()));

        //then
        // 디스크에서 물리 파일이 삭제되었는지 확인
        File deletedFile = new File(todayPath, storedName);
        assertThat(deletedFile.exists()).isFalse(); // 실제 삭제됐는지 확인
    }


    @DisplayName("/article/{id}/edit , POST : 게시글을 업데이드 할 때 삭제하는 파일들이 잘 생성되었는지 확인")
    @Test
    void 게시글_수정_파일생성_성공() throws Exception {
        // 저장된 게시글
        Article savedArticle = articleRepository.save(new Article("기존제목", "기존내용"));
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();

        //파일과 파일DB  (기존에 있던 파일들.)
        String originFileName="삭제파일1.txt";
        String storedName = UUID.randomUUID() + "_" +originFileName;

        File folder = new File(todayPath);
        if (!folder.exists()) folder.mkdirs();
        File actualFile = new File(todayPath, storedName);
        Files.writeString(actualFile.toPath(), "기존 파일 내용",StandardCharsets.UTF_8);

        ArticleAttach attach1 = new ArticleAttach(
            savedArticle,originFileName,storedName,100L,todayPath,"text/plain"); // DB에 파일저장정보 직접 저장
        ArticleAttach saveAttach1 = attachRepository.save(attach1);

        // 수정용 파일
        MockMultipartFile newFile = new MockMultipartFile("files", "newFile.txt", "text/plain",
            "새파일".getBytes());

        //when
        mockMvc.perform(multipart("/article/edit")
                .file(newFile)
                .param("id", savedArticle.getId().toString())
                .param("title", "수정된 제목")
                .param("content", "수정된 내용")
                .param("deleteFileIds", saveAttach1.getId().toString())
                .characterEncoding("UTF-8"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/article/" + savedArticle.getId()));

        //then
        File savedFolder = new File(todayPath);
        assertThat(savedFolder.exists()).isTrue();
        assertThat(savedFolder.isDirectory()).isTrue();
        File[] files = savedFolder.listFiles();
        assertThat(files).isNotNull();
        assertThat(files.length).isEqualTo(1);   //post와 달리 여기서는 MockMultipartiFile 1개.
    }


    @DisplayName("/article/{id}/edit , POST : 게시글을 업데이드 할 때 DB내용들이 잘 변경되었는지 확인")
    @Test
    void 게시글_수정_DB내용_업데이트_성공() throws Exception {
        // 저장된 게시글
        Article savedArticle = articleRepository.save(new Article("기존제목", "기존내용"));
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();

        //파일과 파일DB  (기존에 있던 파일들.)
        String originFileName = "삭제파일1.txt";
        String storedName = UUID.randomUUID() + "_" + originFileName;

        File folder = new File(todayPath);
        if (!folder.exists())
            folder.mkdirs();
        File actualFile = new File(todayPath, storedName);
        Files.writeString(actualFile.toPath(), "기존 파일 내용",StandardCharsets.UTF_8);

        ArticleAttach attach1 = new ArticleAttach(
            savedArticle, originFileName, storedName, 100L, todayPath,
            "text/plain"); // DB에 파일저장정보 직접 저장
        ArticleAttach saveAttach1 = attachRepository.save(attach1);

        // 수정용 파일
        MockMultipartFile newFile = new MockMultipartFile("files", "newFile.txt", "text/plain",
            "새파일".getBytes());

        //when
        mockMvc.perform(multipart("/article/edit")
                .file(newFile)
                .param("id", savedArticle.getId().toString())
                .param("title", "수정된 제목")
                .param("content", "수정된 내용")
                .param("deleteFileIds", saveAttach1.getId().toString())
                .characterEncoding("UTF-8"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/article/" + savedArticle.getId()));


        //then
        //DB에서 Article이 업데이트 잘 되었는지
        Article updatedArticle= articleRepository.findById(savedArticle.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(updatedArticle.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedArticle.getContent()).isEqualTo("수정된 내용");

        // DB에서 첨부파일이 삭제되었는지 확인
        Optional<ArticleAttach> deleted = attachRepository.findById(saveAttach1.getId());
        assertThat(deleted).isEmpty();
        //DB에서 첨부파일 새로 생성되었는지 확인
        List<ArticleAttach> attaches = attachRepository.findByArticle(updatedArticle);
        assertThat(attaches).hasSize(1);
        assertThat(attaches.get(0).getArticle().getId()).isEqualTo(updatedArticle.getId());
        assertThat(attaches.get(0).getOriginalFileName()).isEqualTo("newFile.txt");
    }

    @DisplayName("/article/{id}/delete , POST : 게시글을 삭제 할 때 파일들이 삭제됐는지 확인")
    @Test
    void 게시글_삭제_파일삭제_성공() throws Exception {
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();
        // 저장된 게시글
        Article savedArticle = articleRepository.save(new Article("기존제목", "기존내용"));
        //파일과 파일DB  (기존에 있던 파일들.)
        String originFileName="삭제파일1.txt";
        String storedName = UUID.randomUUID() + "_" +originFileName;
        File folder = new File(todayPath);
        if (!folder.exists()) folder.mkdirs();
        File actualFile = new File(todayPath, storedName);
        Files.writeString(actualFile.toPath(), "기존 파일 내용",StandardCharsets.UTF_8);
        ArticleAttach attach1 = new ArticleAttach(
            savedArticle,originFileName,storedName,100L,todayPath,"text/plain"); // DB에 파일저장정보 직접 저장
        ArticleAttach saveAttach1 = attachRepository.save(attach1);

        //when
        mockMvc.perform(post("/article/"+ savedArticle.getId()+"/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/articles"));

        //then
        File deletedFile = new File(todayPath, storedName);
        assertThat(deletedFile.exists()).isFalse(); // 실제 삭제됐는지 확인
    }

    @DisplayName("/article/{id}/delete , POST : 게시글을 삭제 할 때 DB내용들이 삭제됐는지 호가인")
    @Test
    void 게시글_삭제_DB삭제_성공() throws Exception {
        String todayPath =
            uploadPath + File.separator + articlePath + File.separator + LocalDate.now();
        // 저장된 게시글
        Article savedArticle = articleRepository.save(new Article("기존제목", "기존내용"));
        //파일과 파일DB  (기존에 있던 파일들.)
        String originFileName="삭제파일1.txt";
        String storedName = UUID.randomUUID() + "_" +originFileName;
        File folder = new File(todayPath);
        if (!folder.exists()) folder.mkdirs();
        File actualFile = new File(todayPath, storedName);
        Files.writeString(actualFile.toPath(), "기존 파일 내용",StandardCharsets.UTF_8);
        ArticleAttach attach1 = new ArticleAttach(
            savedArticle,originFileName,storedName,100L,todayPath,"text/plain"); // DB에 파일저장정보 직접 저장
        ArticleAttach saveAttach1 = attachRepository.save(attach1);

        //when
        mockMvc.perform(post("/article/"+ savedArticle.getId()+"/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/articles"));

        assertThat(articleRepository.findById(savedArticle.getId())).isEmpty();
        assertThat(attachRepository.findByArticle(savedArticle)).isEmpty();

    }


}