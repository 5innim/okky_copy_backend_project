package com.innim.okkycopy.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BriefMemberInfo {
    private String nickname;
    private String name;
    private String email;
}
