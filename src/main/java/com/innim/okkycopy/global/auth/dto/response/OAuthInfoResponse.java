package com.innim.okkycopy.global.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OAuthInfoResponse {

    private String provider;
    private String name;
    private String nickname;
    private String email;
    private String profile;

}
