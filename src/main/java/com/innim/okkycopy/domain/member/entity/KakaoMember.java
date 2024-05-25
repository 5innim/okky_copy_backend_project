package com.innim.okkycopy.domain.member.entity;

import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.LinkedHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DiscriminatorValue(value = "kakao")
@Table(name = "kakao_member")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class KakaoMember extends Member {
    @Column(name = "provider_id", nullable = false, unique = true)
    private String providerId;
    @Column(nullable = false, unique = true)
    private String email;

    public static KakaoMember of(OAuthMemberRequest request, CustomOAuth2User oAuth2User) {
        LinkedHashMap<String, String> properties = oAuth2User.getAttribute("properties");
        LinkedHashMap<String, String> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        if (properties == null || kakaoAccount == null) {
            throw new StatusCode500Exception(ErrorCase._500_NULL_PROPERTY);
        }

        return KakaoMember.builder()
            .providerId(oAuth2User.getName())
            .email(kakaoAccount.get("email"))
//            .name(properties.get("name")) TODO "After authenticate business app at kakao, can use name property"
            .name(request.getNickname())
            .nickname(request.getNickname())
            .emailCheck(request.isEmailCheck())
            .role(Role.USER)
            .profile(request.getProfile())
            .build();
    }
}
