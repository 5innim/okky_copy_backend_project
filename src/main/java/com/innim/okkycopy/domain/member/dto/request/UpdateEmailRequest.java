package com.innim.okkycopy.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateEmailRequest {
    @Email
    private String email;
}
