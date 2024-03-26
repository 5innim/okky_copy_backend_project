package com.innim.okkycopy.domain.member.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberResponse {

    private long memberId;
    private String nickname;
    private List<Long> scrappedPost;

}
