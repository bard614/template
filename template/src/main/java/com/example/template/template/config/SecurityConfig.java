package com.example.template.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll() // 允許匿名訪問 "/"
                .anyRequest().authenticated() // 其他所有請求都需要認證
            )
            .formLogin(formLogin -> formLogin
                .permitAll() // 允許所有用戶訪問登入頁面
            )
            .logout(logout -> logout
                .permitAll()); // 允許所有用戶訪問登出

        return http.build();
    }
}