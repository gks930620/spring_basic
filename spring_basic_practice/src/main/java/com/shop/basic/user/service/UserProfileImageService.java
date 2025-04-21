package com.shop.basic.user.service;

import com.shop.basic.user.dto.UserProfileImageDTO;
import com.shop.basic.user.repository.UserProfileImageRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileImageService {
    private  final UserProfileImageRepository userProfileImageRepository;

    @Transactional
    public List<UserProfileImageDTO> getProfileImages(List<Long> deleteIds){
        if( deleteIds !=null &&  !deleteIds.isEmpty()  ){
            return userProfileImageRepository.findAllById(deleteIds).stream().map(UserProfileImageDTO::fromEntity).toList();
        }
        return Collections.emptyList();
    }


    public UserProfileImageDTO getProfileImage(Long id) {
        return userProfileImageRepository.findById(id).map(UserProfileImageDTO::fromEntity).orElseThrow(
            EntityNotFoundException::new);
    }
}
