package com.innim.okkycopy.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    @Pattern(regexp = "^[A-Za-z가-힣]{2,20}$")
    private String name;
    @Pattern(regexp = "^[A-Za-z가-힣0-9]{2,20}$")
    private String nickname;
    private String profile;
}
