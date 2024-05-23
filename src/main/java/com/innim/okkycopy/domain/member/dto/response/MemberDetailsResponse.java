package com.innim.okkycopy.domain.member.dto.response;

import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.entity.KakaoMember;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.NaverMember;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
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
    private String id;
    private String nickname;
    private List<Long> scrappedPost;
    private String profile;
    private String name;
    private String accountFrom;

    public static MemberDetailsResponse from(Member member) {
        List<Long> scrappedPostIdList = new ArrayList<>();
        List<Scrap> scraps = member.getScrapList();
        if (scraps != null) {
            for (Scrap scrap : scraps) {
                scrappedPostIdList.add(scrap.getPost().getPostId());
            }
        }

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
            .id((member instanceof OkkyMember okkyMember) ? okkyMember.getId() : null)
            .nickname(member.getNickname())
            .name(member.getName())
            .accountFrom(accountFrom)
            .profile(member.getProfile())
            .scrappedPost(scrappedPostIdList)
            .build();
    }

}
