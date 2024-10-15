package com.innim.okkycopy.domain.member.entity;

import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.global.auth.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "okky_member")
@SuperBuilder
@DynamicUpdate
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class OkkyMember extends Member {

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    public static OkkyMember of(MemberRequest request, PasswordEncoder passwordEncoder) {
        return OkkyMember.builder()
            .id(request.getId())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .name(request.getName())
            .nickname(request.getNickname())
            .emailCheck(request.isEmailCheck())
            .role(Role.MAIL_INVALID_USER)
            .profile(null)
            .build();
    }

}
