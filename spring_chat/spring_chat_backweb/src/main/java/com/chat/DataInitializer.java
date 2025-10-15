package com.chat;

import com.chat.security.entity.UserEntity;
import com.chat.security.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // 이미 데이터가 있으면 초기화하지 않음
        if (userRepository.count() > 0) {
            return;
        }

        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("pass1"));
        user1.setRoles(List.of("USER"));

        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("pass2"));
        user2.setRoles(List.of("USER"));

        UserEntity user3 = new UserEntity();
        user3.setUsername("user3");
        user3.setPassword(passwordEncoder.encode("pass3"));
        user3.setRoles(List.of("USER"));

        userRepository.saveAll(List.of(user1, user2, user3));
        System.out.println("✅ 초기 사용자 3명 등록 완료!");



    }
}
