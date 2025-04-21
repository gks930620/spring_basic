package com.shop.basic.user.controller;

import com.shop.basic.user.dto.UserProfileImageDTO;
import com.shop.basic.user.entity.UserProfileImage;
import com.shop.basic.user.service.UserProfileImageService;
import jakarta.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class UserProfileImageController {

    private final UserProfileImageService userProfileImageService;

    @GetMapping("/profile-image/{id}")
    @ResponseBody
    public ResponseEntity<Resource> showProfileImage(@PathVariable Long id) throws IOException {
        UserProfileImageDTO profileImageDTO=userProfileImageService.getProfileImage(id);
        Path path = Paths.get(profileImageDTO.getFilePath(), profileImageDTO.getStoredFileName());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, profileImageDTO.getContentType())
            .body(resource);
    }

}
