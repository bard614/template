package com.example.template.template.config;

import com.example.template.template.model.User;
import com.example.template.template.service.UserLogService;
import com.example.template.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLogService userLogService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll() // 允許匿名訪問 "/"
                .requestMatchers("/api/users").hasRole("ADMIN") // 只有 ADMIN 角色可以訪問 /api/users
                .anyRequest().authenticated() // 其他所有請求都需要認證
            )
            .formLogin(formLogin -> formLogin
                .permitAll() // 允許所有用戶訪問登入頁面
                .successHandler(authenticationSuccessHandler()) // 配置登入成功處理器
            )
            .logout(logout -> logout
                .permitAll() // 允許所有用戶訪問登出
                .logoutSuccessHandler(logoutSuccessHandler())); // 配置登出成功處理器

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                if (user != null) {
                    userLogService.logUserOperation(user, "LOGIN");
                }
                response.sendRedirect("/users.html"); // 登入成功後重定向到使用者管理頁面
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
                if (authentication != null) {
                    String username = authentication.getName();
                    User user = userService.findByUsername(username);
                    if (user != null) {
                        userLogService.logUserOperation(user, "LOGOUT");
                    }
                }
                response.sendRedirect("/login?logout"); // 登出成功後重定向到登入頁面
            }
        };
    }
}