package com.chat.security.service;


import com.chat.security.JwtUtil;
import com.chat.security.entity.RefreshEntity;
import com.chat.security.repository.RefreshRepository;
import com.chat.security.repository.UserRepository;
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
