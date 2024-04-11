package com.innim.okkycopy.global.config;

import com.innim.okkycopy.domain.member.service.MemberLoginService;
import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.auth.filter.CorsFilter;
import com.innim.okkycopy.global.auth.filter.FormDataLoginAuthenticationFilter;
import com.innim.okkycopy.global.auth.filter.HandleStatusCodeExceptionFilter;
import com.innim.okkycopy.global.auth.filter.JwtAuthenticationFilter;
import com.innim.okkycopy.global.auth.filter.RefreshJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final MemberLoginService memberLoginService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(AbstractHttpConfigurer::disable)
            .headers(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .servletApi(AbstractHttpConfigurer::disable)
            .anonymous(AbstractHttpConfigurer::disable)
            .exceptionHandling(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request -> {
                request.requestMatchers(HttpMethod.POST,
                        "/board/knowledge/write",
                        "/board/community/write",
                        "/board/post/scrap",
                        "/board/posts/{id}/comment",
                        "/board/posts/{postId}/comments/{commentId}/recomment",
                        "/board/posts/{id}/like",
                        "/board/posts/{id}/hate",
                        "/board/comments/{id}/like",
                        "/board/comments/{id}/hate",
                        "/board/file/upload").hasAnyAuthority(Role.USER.getValue(), Role.ADMIN.getValue())
                    .requestMatchers(HttpMethod.DELETE,
                        "/board/post/scrap",
                        "/board/knowledge/posts/{id}",
                        "/board/community/posts/{id}",
                        "/board/comments/{id}",
                        "/board/posts/{id}/like",
                        "/board/posts/{id}/hate",
                        "/board/comments/{id}/like",
                        "/board/comments/{id}/hate").hasAnyAuthority(Role.USER.getValue(), Role.ADMIN.getValue())
                    .requestMatchers(HttpMethod.PUT,
                        "/board/knowledge/posts/{id}",
                        "/board/community/posts/{id}",
                        "/board/comments/{id}").hasAnyAuthority(Role.USER.getValue(), Role.ADMIN.getValue())
                    .requestMatchers(HttpMethod.GET,
                        "/board/topics",
                        "/board/knowledge/posts/{id}",
                        "/board/community/posts/{id}",
                        "/member/info",
                        "/board/posts/{id}/comments",
                        "/board/comments/{id}/recomments",
                        "/board/knowledge/posts",
                        "/board/community/posts").permitAll()
                    .requestMatchers(HttpMethod.POST, "/member/signup").permitAll();
            }).apply(new CustomDsl());

        return http.build();
    }


    public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) {
            http.addFilterAt(new HandleStatusCodeExceptionFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new FormDataLoginAuthenticationFilter(http.getSharedObject(
                        AuthenticationManager.class), memberLoginService),
                    HandleStatusCodeExceptionFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(customUserDetailsService),
                    FormDataLoginAuthenticationFilter.class)
                .addFilterAfter(new RefreshJwtFilter(memberLoginService),
                    JwtAuthenticationFilter.class)
                .addFilterBefore(new CorsFilter(), SecurityContextHolderFilter.class);
        }
    }
}
