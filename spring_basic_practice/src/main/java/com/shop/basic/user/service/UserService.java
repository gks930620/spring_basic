package com.shop.basic.user.service;

import com.shop.basic.user.dto.UserProfileImageDTO;
import com.shop.basic.user.dto.request.UserJoinRequest;
import com.shop.basic.user.dto.request.UserUpdateRequest;
import com.shop.basic.user.dto.response.UserInfoResponse;
import com.shop.basic.user.entity.UserEntity;
import com.shop.basic.user.entity.UserProfileImage;
import com.shop.basic.user.repository.UserProfileImageRepository;
import com.shop.basic.user.repository.UserRepository;
import com.shop.basic.util.FileUtil;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private  final UserProfileImageRepository userProfileImageRepository;

    @Transactional
    public Long saveUser(UserJoinRequest dto,  List<UserProfileImageDTO> userProfileDTOS)  throws IOException {
        UserEntity user = UserEntity.fromJoinRequest(dto);
        userRepository.save(user);
        userProfileImageRepository.saveAll( userProfileDTOS.stream().map(UserProfileImage::fromDTO)
            .peek( attach -> attach.setUser(user)).toList() );
        return user.getId();
    }


    public UserInfoResponse getUser( String username){
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(EntityNotFoundException::new);
        List<UserProfileImage> profileImages = userProfileImageRepository.findByUser(user);
        return UserInfoResponse.fromEntity(user,profileImages);
    }



    @Transactional
    public void updateUser(String username,UserUpdateRequest dto,
        List<Long> deleteImageIds,
        List<UserProfileImageDTO>  profileImageDTOS) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if( deleteImageIds !=null &&  !deleteImageIds.isEmpty()  ){
            userProfileImageRepository.deleteAllById(deleteImageIds);
        }
        user.update(dto);
        userProfileImageRepository.saveAll(profileImageDTOS.stream().map(UserProfileImage::fromDTO).
            peek( attach -> attach.setUser(user)).toList());
    }


}