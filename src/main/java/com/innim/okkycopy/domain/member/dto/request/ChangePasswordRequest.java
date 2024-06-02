package com.innim.okkycopy.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String oldPassword;
    @Pattern(regexp = "^(?=\\S*[0-9])(?=\\S*[a-zA-Z])\\S{6,20}$")
    private String newPassword;
}
