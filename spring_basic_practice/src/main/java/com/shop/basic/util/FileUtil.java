package com.shop.basic.util;

import com.shop.basic.article.dto.ArticleAttachDTO;
import com.shop.basic.article.entity.ArticleAttach;
import com.shop.basic.user.dto.UserProfileImageDTO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
    // Article 파일 삭제 유틸
    public static void deleteFile(String uploadPath, String storedFileName) {
        File file = new File(uploadPath, storedFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static <T> List<T> saveFilesWith(
        List<MultipartFile> multipartFiles,
        String uploadPath,
        Function<FileMetaData, T> convertToDto
    ) throws IOException {
        List<T> results = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty()) return results;

        for (MultipartFile file : multipartFiles) {
            if (file != null && !file.isEmpty()) {
                FileMetaData meta = saveFile(file, uploadPath);
                results.add(convertToDto.apply(meta));
            }
        }

        return results;
    }
    private static FileMetaData saveFile(MultipartFile file, String uploadPath) throws IOException {
        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;
        Long size = file.getSize();
        String contentType = file.getContentType();

        File folder = new File(uploadPath);
        if (!folder.exists()) folder.mkdirs();

        File saveFile = new File(uploadPath, storedName);
        file.transferTo(saveFile);

        return new FileMetaData(originalName, storedName, size, uploadPath, contentType);
    }


    public record FileMetaData(
        String originalFileName,
        String storedFileName,
        Long size,
        String filePath,
        String contentType
    ) {}

}