package com.innim.okkycopy.domain.member.dto.response;

import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDetailsResponse {

    private long memberId;
    private String nickname;
    private List<Long> scrappedPost;
    private String profile;
    private String name;

    public static MemberDetailsResponse from(Member member) {
        List<Long> scrappedPostIdList = new ArrayList<>();
        List<Scrap> scraps = member.getScrapList();
        if (scraps != null) {
            for (Scrap scrap : scraps) {
                scrappedPostIdList.add(scrap.getPost().getPostId());
            }
        }

        return MemberDetailsResponse.builder()
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .name(member.getName())
            .profile(member.getProfile())
            .scrappedPost(scrappedPostIdList)
            .build();
    }

}
