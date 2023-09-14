package com.innim.okkycopy.domain.member.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
public class SignupRequest {
    private String id;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private boolean emailCheck;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
