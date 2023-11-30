package com.innim.okkycopy.global.config;

import com.innim.okkycopy.global.auth.AuthService;
import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.auth.filter.IdPasswordAuthenticationFilter;
import com.innim.okkycopy.global.auth.filter.JwtAuthenticationFilter;
import com.innim.okkycopy.global.auth.filter.JwtRefreshFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.ServletApiConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(configure -> sessionManagementConfigure(configure))
            .headers(configure -> headersConfigure(configure))
            .csrf(configure -> csrfConfigure(configure))
            .logout(configure -> logoutConfigure(configure))
            .servletApi(configure -> servletApiConfigure(configure))
            .anonymous(configure -> anonymousConfigure(configure))
            .exceptionHandling(configure -> exceptionHandlingConfigure(configure))
            .authorizeHttpRequests(request -> {
                request.requestMatchers(HttpMethod.POST,
                        "/board/knowledge/write",
                        "/board/post/scrap").hasAnyAuthority(Role.USER.getValue(), Role.ADMIN.getValue())
                    .requestMatchers(HttpMethod.DELETE,
                        "/board/post/scrap").hasAnyAuthority(Role.USER.getValue(), Role.ADMIN.getValue())
                    .requestMatchers(HttpMethod.GET,
                        "/board/topics",
                        "/board/knowledge/posts/{id}").permitAll()
                    .requestMatchers(HttpMethod.POST, "/member/signup").permitAll();
            })
            .apply(new CustomDsl());

        return http.build();
    }

    private SessionManagementConfigurer<HttpSecurity> sessionManagementConfigure(
        SessionManagementConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private HeadersConfigurer<HttpSecurity> headersConfigure(
        HeadersConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private CsrfConfigurer<HttpSecurity> csrfConfigure(CsrfConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private LogoutConfigurer<HttpSecurity> logoutConfigure(
        LogoutConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private ServletApiConfigurer<HttpSecurity> servletApiConfigure(
        ServletApiConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private AnonymousConfigurer<HttpSecurity> anonymousConfigure(
        AnonymousConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigure(
        ExceptionHandlingConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.addFilterAt(new IdPasswordAuthenticationFilter(http.getSharedObject(
                        AuthenticationManager.class), authService),
                    UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(userDetailsService),
                    IdPasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtRefreshFilter(authService),
                    JwtAuthenticationFilter.class);
        }

    }
}
