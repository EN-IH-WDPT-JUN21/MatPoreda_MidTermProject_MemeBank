package com.ironhack.MemeBank.security;

import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;



import java.util.Collection;
import java.util.HashSet;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities=new HashSet<>();
//        for(UserRoles role : user.getUserRoles()){
//            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole().getName()));
//    }
        authorities.add(new SimpleGrantedAuthority("ROLE_"+user.getRole().getName()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
