package com.innim.okkycopy.domain.member.dto.response;

import com.innim.okkycopy.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberBriefResponse {

    private String nickname;
    private String name;
    private String email;

    public static MemberBriefResponse from(Member member) {
        return MemberBriefResponse.builder()
            .nickname(member.getNickname())
            .name(member.getName())
            .email(member.getEmail())
            .build();
    }
}
