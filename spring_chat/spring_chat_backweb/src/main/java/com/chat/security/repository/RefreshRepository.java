package com.chat.security.repository;

import com.chat.security.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository  extends JpaRepository<RefreshEntity,Long> {
    public void deleteByToken(String token);
    public RefreshEntity findByToken(String token);
}
