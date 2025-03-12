package com.security.jwt.service;


import com.security.jwt.JwtUtil;
import com.security.jwt.entity.RefreshEntity;
import com.security.jwt.entity.UserEntity;
import com.security.jwt.repository.RefreshRepository;
import com.security.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private  final RefreshRepository refreshRepository;
    private  final UserRepository userRepository;
    private  final JwtUtil jwtUtil;


    @Transactional(readOnly = false)
    public RefreshEntity getRefresh(String token){
        return refreshRepository.findByToken(token);
    }

    @Transactional(readOnly = false)
    public void saveRefresh(String token){
        RefreshEntity refreshEntity = new RefreshEntity();
        String username = jwtUtil.extractUsername(token);
        refreshEntity.setUserEntity(userRepository.findByUsername(username));
        refreshEntity.setToken(token);
        refreshRepository.save(refreshEntity);
    }
    @Transactional(readOnly = false)
    public void deleteRefresh(String token){
            refreshRepository.deleteByToken(token);
    }
}
