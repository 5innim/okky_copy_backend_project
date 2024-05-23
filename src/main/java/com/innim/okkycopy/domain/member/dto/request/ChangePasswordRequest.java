package com.innim.okkycopy.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String oldPassword;
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String newPassword;
}
