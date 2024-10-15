package com.innim.okkycopy.domain.board.dto.response.post;

import com.innim.okkycopy.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class WriterInfo {

    private long memberId;
    private String nickname;
    private String profile;

    public static WriterInfo from(Member member) {
        return WriterInfo.builder()
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .profile(member.getProfile())
            .build();
    }
}
