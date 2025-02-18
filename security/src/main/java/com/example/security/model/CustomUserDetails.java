package com.example.security.model;

import com.example.security.entity.UserEntity;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private UserEntity userEntity;   //엔티티필드.  사실 UserDTO필드를 만드는게 맞음.
    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        for(String role : userEntity.getRoles()){
            collection.add(  ()-> role );  //new GrantedAuthority
        }
        return collection;
    }




    //isAccountNonExpired ,  isAccountNonLocked , isCredentialsNonExpired ,  isEnabled
    //spring boot 2 에서는 필수로 구현했지만,  3부터는 userDetials가  default 메소드

}
