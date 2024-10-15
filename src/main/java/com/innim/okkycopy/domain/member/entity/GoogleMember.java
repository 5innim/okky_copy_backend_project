package com.innim.okkycopy.domain.member.entity;


import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.auth.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DiscriminatorValue(value = "google")
@Table(name = "google_member")
@SuperBuilder
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleMember extends Member {

    @Column(name = "provider_id", nullable = false, unique = true)
    private String providerId;

    @Column(nullable = false, unique = true)
    private String email;

    public static GoogleMember of(OAuthMemberRequest request, CustomOAuth2User oAuth2User) {
        return GoogleMember.builder()
            .providerId(oAuth2User.getName())
            .email(oAuth2User.getAttribute("email"))
            .name(oAuth2User.getAttribute("name"))
            .nickname(request.getNickname())
            .emailCheck(request.isEmailCheck())
            .role(Role.MAIL_INVALID_USER)
            .profile(request.getProfile())
            .build();
    }
}
