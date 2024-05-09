package com.innim.okkycopy.global.config;

import com.innim.okkycopy.domain.member.service.MemberLoginService;
import com.innim.okkycopy.global.auth.CustomOAuth2AuthenticationSuccessHandler;
import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.auth.filter.CorsFilter;
import com.innim.okkycopy.global.auth.filter.FormDataLoginAuthenticationFilter;
import com.innim.okkycopy.global.auth.filter.HandleStatusCodeExceptionFilter;
import com.innim.okkycopy.global.auth.filter.JwtAuthenticationFilter;
import com.innim.okkycopy.global.auth.filter.OAuth2SessionInfoProcessingFilter;
import com.innim.okkycopy.global.auth.filter.RefreshJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final MemberLoginService memberLoginService;
    private final CustomOAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    @Value("#{environment['frontend.origin']}")
    private String frontendOrigin;

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
            .oauth2Login(oauth2 -> {
                oauth2
                    .successHandler(oAuth2AuthenticationSuccessHandler);
            })
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
                        "/member/info").hasAnyAuthority(Role.USER.getValue(), Role.ADMIN.getValue())
                    .requestMatchers(HttpMethod.GET,
                        "/board/topics",
                        "/board/knowledge/posts/{id}",
                        "/board/community/posts/{id}",
                        "/board/posts/{id}/comments",
                        "/board/comments/{id}/recomments",
                        "/board/knowledge/posts",
                        "/board/community/posts").permitAll()
                    .requestMatchers(HttpMethod.POST,
                        "/member/signup",
                        "/member/{provider}/signup").permitAll();
            }).apply(new CustomDsl());

        return http.build();
    }


    public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {


        @Override
        public void configure(HttpSecurity http) {
            http.addFilterBefore(new CorsFilter(frontendOrigin), SecurityContextHolderFilter.class)
                .addFilterAfter(new HandleStatusCodeExceptionFilter(), DefaultLoginPageGeneratingFilter.class)
                .addFilterAfter(new OAuth2SessionInfoProcessingFilter(), HandleStatusCodeExceptionFilter.class)
                .addFilterAfter(new FormDataLoginAuthenticationFilter(http.getSharedObject(
                        AuthenticationManager.class), memberLoginService),
                    OAuth2SessionInfoProcessingFilter.class)
                .addFilterAfter(new RefreshJwtFilter(memberLoginService),
                    FormDataLoginAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(customUserDetailsService, new RequestMatcher[]{
                        new AntPathRequestMatcher("/member/{provider}/signup", "POST")
                    }),
                    RefreshJwtFilter.class);
        }
    }
}
