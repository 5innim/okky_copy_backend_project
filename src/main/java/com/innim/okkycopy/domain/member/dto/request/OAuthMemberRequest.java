package com.innim.okkycopy.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthMemberRequest {

    private String key;
    @Pattern(regexp = "^[A-Za-z가-힣0-9ㄱ-ㅎㅏ-ㅣ]{2,20}$")
    private String nickname;
    private String profile;
    private boolean emailCheck;
}
