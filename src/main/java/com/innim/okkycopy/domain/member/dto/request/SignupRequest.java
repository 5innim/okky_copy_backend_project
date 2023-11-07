package com.innim.okkycopy.domain.member.dto.request;

import com.innim.okkycopy.global.error.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    @Size(min = 4, max = 15)
    private String id;
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String password;
    @Email
    private String email;
    @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$")
    private String name;
    @Pattern(regexp = "^[A-Za-z가-힣0-9]{2,20}$")
    private String nickname;
    private boolean emailCheck;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    public static ErrorCode retreiveErrorCode(String field) {
        switch(field) {
            case("id"):
                return ErrorCode._400_INVALID_ID;
            case("password"):
                return ErrorCode._400_INVALID_PW;
            case("email"):
                return ErrorCode._400_INVALID_EMAIL;
            case("name"):
                return ErrorCode._400_INVALID_NAME;
            case("nickname"):
                return ErrorCode._400_INVALID_NICKNAME;
            default:
                return ErrorCode._400_INVALID_UNEXPECTED;
        }
    }
}
