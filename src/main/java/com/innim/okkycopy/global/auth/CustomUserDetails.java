package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Member member;

    public long getUserId() {
        return member.getMemberId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> member.getRole().getValue());
    }

    @Override
    public String getPassword() {
        if (member instanceof OkkyMember) {
            return ((OkkyMember) member).getPassword();
        }

        return "";
    }

    @Override
    public String getUsername() {
        if (member instanceof OkkyMember) {
            return ((OkkyMember) member).getId();
        }

        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
