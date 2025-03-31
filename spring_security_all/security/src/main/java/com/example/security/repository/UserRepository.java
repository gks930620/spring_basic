package com.example.security.repository;

import com.example.security.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public   UserEntity findByUsername(String username);    //join하는데는 필요없지만, 나중에 로그인할 때 쓰려고 미리만듦.
}
