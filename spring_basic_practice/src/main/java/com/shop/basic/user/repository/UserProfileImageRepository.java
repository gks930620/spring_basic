package com.shop.basic.user.repository;

import com.shop.basic.user.entity.UserEntity;
import com.shop.basic.user.entity.UserProfileImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {

    public List<UserProfileImage> findByUser(UserEntity user);
}
