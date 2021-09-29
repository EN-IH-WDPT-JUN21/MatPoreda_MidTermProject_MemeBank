package com.ironhack.MemeBank.security;

import com.ironhack.MemeBank.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//        @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("admin").password(passwordEncoder.encode("123456")).roles("ADMIN", "USER")
//                .and()
//                .withUser("user").password(passwordEncoder.encode("123456")).roles("USER");
//
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.httpBasic();
        http.csrf().disable();
        http.authorizeRequests()
                //*********GET********
                //accounts
                .mvcMatchers(HttpMethod.GET, "/accounts").hasAnyRole("ACCOUNT_HOLDER")  //return owned accounts
                .mvcMatchers(HttpMethod.GET, "/accounts/all").hasAnyRole("ADMIN") // return all accounts
                //transactions
                .mvcMatchers(HttpMethod.GET, "/transactions").hasAnyRole("ACCOUNT_HOLDER")//return owned transactions
                .mvcMatchers(HttpMethod.GET, "/transactions/all").hasAnyRole("ADMIN") // return all transactions
                //users
                .mvcMatchers(HttpMethod.GET, "/users").hasAnyRole("ADMIN")  //return all users
                .mvcMatchers(HttpMethod.GET, "/users/admins").hasAnyRole("ADMIN") // return all admins
                .mvcMatchers(HttpMethod.GET, "/users/third_party").hasAnyRole("ADMIN", "THIRD_PARTY") // return all third_party
                .mvcMatchers(HttpMethod.GET, "/users/account_holders").hasAnyRole("ADMIN") // return all account_holders

                //*********POST********
                //accounts
                .mvcMatchers(HttpMethod.POST, "/accounts").hasAnyRole("ADMIN") //create new accounts
                .mvcMatchers(HttpMethod.POST, "/accounts/set_balance/{id}").hasAnyRole("ADMIN") //modify account balance
                //transactions
                .mvcMatchers(HttpMethod.POST, "/transactions/third_party").hasAnyRole("THIRD_PARTY") //create new third_party transaction
                .mvcMatchers(HttpMethod.POST, "/transactions/{id}").hasAnyRole("ACCOUNT_HOLDER") //transfer funds from owned accounts
                //users
                .mvcMatchers(HttpMethod.POST, "/users").hasAnyRole("ADMIN") //create new users

                .anyRequest().permitAll();
    }

}