package com.innim.okkycopy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.ServletApiConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(configure -> sessionManagementConfigure(configure))
            .headers(configure -> headersConfigure(configure))
            .csrf(configure -> csrfConfigure(configure))
            .logout(configure -> logoutConfigure(configure))
            .servletApi(configure -> servletApiConfigure(configure))
            .anonymous(configure -> anonymousConfigure(configure))
            .exceptionHandling(configure -> exceptionHandlingConfigure(configure));

        return http.build();
    }

    private SessionManagementConfigurer<HttpSecurity> sessionManagementConfigure(SessionManagementConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private HeadersConfigurer<HttpSecurity> headersConfigure(HeadersConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private CsrfConfigurer<HttpSecurity> csrfConfigure(CsrfConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private LogoutConfigurer<HttpSecurity> logoutConfigure(LogoutConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private ServletApiConfigurer<HttpSecurity> servletApiConfigure(ServletApiConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private AnonymousConfigurer<HttpSecurity> anonymousConfigure(AnonymousConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }

    private ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigure(ExceptionHandlingConfigurer<HttpSecurity> configure) {
        configure.disable();
        return configure;
    }
}
