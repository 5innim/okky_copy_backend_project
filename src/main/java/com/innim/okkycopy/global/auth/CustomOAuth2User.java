package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.entity.Member;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Member member;
    private final String registrationId;
    private final boolean isSignedUpBefore;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes, String nameAttributeKey, Member member, String registrationId) {
        super(authorities, attributes, nameAttributeKey);
        this.isSignedUpBefore = member != null;
        this.member = member;
        this.registrationId = registrationId;
    }

    public static CustomOAuth2User of(OAuth2User oAuth2User, String nameAttributeKey, Member member,
        String registrationId) {
        return new CustomOAuth2User(
            oAuth2User.getAuthorities(),
            oAuth2User.getAttributes(),
            nameAttributeKey,
            member,
            registrationId);
    }
}
