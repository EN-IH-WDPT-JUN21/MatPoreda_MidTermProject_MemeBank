package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.repository.UserRepository;
import com.ironhack.MemeBank.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user =userRepository.findByUsername(s);
        if(!user.isPresent()){
            throw new UsernameNotFoundException("User not found");
        }else{

        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user.get());
        return customUserDetails;
    }
}
