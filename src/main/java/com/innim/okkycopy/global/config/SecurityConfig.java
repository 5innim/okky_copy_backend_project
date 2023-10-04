package com.innim.okkycopy.global.config;

import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.auth.filter.IdPasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class SecurityConfig {

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
            .apply(new CustomDsl());


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

    public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.addFilterAt(new IdPasswordAuthenticationFilter(http.getSharedObject(
                AuthenticationManager.class)), UsernamePasswordAuthenticationFilter.class);
        }

    }
}
