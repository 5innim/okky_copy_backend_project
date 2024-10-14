package com.innim.okkycopy.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRequest {

    @Size(min = 4, max = 15)
    private String id;
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String password;
    @Email
    private String email;
    @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$")
    private String name;
    @Pattern(regexp = "^[A-Za-z가-힣0-9ㄱ-ㅎㅏ-ㅣ]{2,20}$")
    private String nickname;
    private boolean emailCheck;


}
