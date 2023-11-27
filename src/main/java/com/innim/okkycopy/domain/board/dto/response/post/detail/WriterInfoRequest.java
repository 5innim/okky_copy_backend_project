package com.innim.okkycopy.domain.board.dto.response.post.detail;

import com.innim.okkycopy.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WriterInfoRequest {
    private long memberId;
    private String nickName;
    private String profile;

    public static WriterInfoRequest toWriterInfoRequestDto(Member member) {
        return WriterInfoRequest.builder()
            .memberId(member.getMemberId())
            .nickName(member.getNickname())
            .profile(member.getProfile())
            .build();
    }
}
