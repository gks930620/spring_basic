package com.shop.basic.user.controller;

import com.shop.basic.user.dto.UserProfileImageDTO;
import com.shop.basic.user.dto.request.UserJoinRequest;
import com.shop.basic.user.dto.request.UserUpdateRequest;
import com.shop.basic.user.dto.response.UserInfoResponse;
import com.shop.basic.user.service.UserProfileImageService;
import com.shop.basic.user.service.UserService;
import com.shop.basic.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Value("${file.upload.path}")
    private String uploadPath;
    private static  final String userProfilePath="user_profile";

    private final UserService userService;
    private  final UserProfileImageService userProfileImageService;

    /** 회원가입 폼 */
    @GetMapping("/join")
    public String joinForm() {
        return "user/join-form";
    }

    /** 회원가입 처리 */
    @PostMapping("/join")
    public String joinSubmit(UserJoinRequest dto,
        @RequestParam("profileImages") List<MultipartFile> multipartFiles) throws IOException {
        
        List<UserProfileImageDTO> userProfileDTOS = FileUtil.saveFilesWith(multipartFiles,
            uploadPath + File.separator + userProfilePath + File.separator + LocalDate.now()
        ,meta -> new UserProfileImageDTO(null, meta.originalFileName(), meta.storedFileName(),
                meta.size(), meta.filePath(), meta.contentType()));
        
        userService.saveUser(dto,userProfileDTOS);
        return "redirect:/user/myinfo?username=" +dto.getUsername();
    }

    /** 내 정보 보기.   security하기전에 그냥 getUser로. */
    @GetMapping("/myinfo")
    public String myInfo(@RequestParam("username")  String username, Model model) {
        UserInfoResponse user = userService.getUser(username);
        model.addAttribute("user",user);
        return "user/myinfo";
    }


    /** 내 정보 수정 폼 */
    @GetMapping("/edit")
    public String editForm(@RequestParam("username")  String username, Model model) {
        UserInfoResponse user = userService.getUser(username);
        model.addAttribute("user",user);
        return "user/edit-form";
    }


    /** 내 정보 수정 처리 */
    @PostMapping("/edit")
    public String editSubmit(
        @RequestParam("username") String username,   //파라미터가 아니라 로그인사용자가 될거임.
        @ModelAttribute UserUpdateRequest dto,
        @RequestParam(name = "deleteImageIds",required = false) List<Long> deleteImageIds,
        @RequestParam(name = "newProfileImages", required = false) List<MultipartFile> newProfileImages) throws IOException {
        List<UserProfileImageDTO> deleteProfileImages = userProfileImageService.getProfileImages(deleteImageIds);
        deleteProfileImages.forEach(attach -> FileUtil.deleteFile(uploadPath,attach.getStoredFileName()) );

        List<UserProfileImageDTO> userProfileDTOS = FileUtil.saveFilesWith(newProfileImages,
            uploadPath + File.separator + userProfilePath + File.separator + LocalDate.now()
            ,meta -> new UserProfileImageDTO(null, meta.originalFileName(), meta.storedFileName(),
                meta.size(), meta.filePath(), meta.contentType()));

        userService.updateUser(username,dto, deleteImageIds, userProfileDTOS);
        return "redirect:/user/myinfo?username=" +username;
    }




}