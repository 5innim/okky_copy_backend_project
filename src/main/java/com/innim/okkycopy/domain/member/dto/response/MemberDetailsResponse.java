package com.innim.okkycopy.domain.member.dto.response;

import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.entity.KakaoMember;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.NaverMember;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDetailsResponse {

    private long memberId;
    private String nickname;
    private String profile;
    private String name;
    private String accountFrom;
    private String role;
    private String email;

    public static MemberDetailsResponse from(Member member) {

        // TODO: if new oauth provider is added, then should expand this logic
        String accountFrom = "";
        if (member instanceof OkkyMember) {
            accountFrom = "okky";
        } else if (member instanceof GoogleMember) {
            accountFrom = "google";
        } else if (member instanceof NaverMember) {
            accountFrom = "naver";
        } else if (member instanceof KakaoMember) {
            accountFrom = "kakao";
        }

        return MemberDetailsResponse.builder()
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .name(member.getName())
            .accountFrom(accountFrom)
            .role(member.getRole().getValue())
            .email(member.findEmail())
            .profile(member.getProfile())
            .build();
    }

}
