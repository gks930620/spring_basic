package com.shop.basic.user.repository;

import com.shop.basic.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    public Optional<UserEntity> findByUsername(String username);
}
