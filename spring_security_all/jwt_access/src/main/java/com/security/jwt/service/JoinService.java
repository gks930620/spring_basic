package com.security.jwt.service;

import com.security.jwt.entity.UserEntity;
import com.security.jwt.model.JoinDTO;
import com.security.jwt.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void joinProcess(JoinDTO joinDTO) {
        //db에 이미 동일한 username을 가진 회원이 존재하는지?
        UserEntity find = userRepository.findByUsername(joinDTO.getUsername());
        if(find!=null) {
            System.out.println("이미 있는 ID입니다.");
        }
        UserEntity user = new UserEntity();
        user.setUsername(joinDTO.getUsername());
        user.setPassword(passwordEncoder.encode(joinDTO.getPassword()));  //DB에 저장될 때는 반드시 encoding되서 저장되어야한다.
        user.getRoles().add("USER");     //hasAuthority("USER") 에 맞게 USER로 세팅

        userRepository.save(user);
    }
}