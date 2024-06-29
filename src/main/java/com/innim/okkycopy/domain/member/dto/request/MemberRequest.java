package com.innim.okkycopy.domain.member.dto.request;

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
public class MemberRequest {

    @Size(min = 4, max = 15)
    private String id;
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String password;
    @Email
    private String email;
    @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$")
    private String name;
    @Pattern(regexp = "^([A-Za-z가-힣0-9]*|[ㄱ-ㅎㅏ-ㅣ]+){2,20}$")
    private String nickname;
    private boolean emailCheck;


}
