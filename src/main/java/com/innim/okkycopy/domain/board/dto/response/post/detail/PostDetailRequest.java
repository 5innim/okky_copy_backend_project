package com.innim.okkycopy.domain.board.dto.response.post.detail;

import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostDetailRequest {
    private WriterInfoRequest writerInfo;
    private String title;
    private String content;
    private List<String> tags;
    private int views;
    private int likes;
    private LocalDateTime createdDate;

    public static PostDetailRequest toPostDetailRequestDto(KnowledgePost knowledgePost, Member member) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : knowledgePost.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailRequest.builder()
            .writerInfo(WriterInfoRequest.builder()
                    .memberId(member.getMemberId())
                    .nickName(member.getNickname())
                    .profile(member.getProfile()).build())
            .title(knowledgePost.getTitle())
            .content(knowledgePost.getContent())
            .tags(tags)
            .views(knowledgePost.getViews())
            .likes(knowledgePost.getLikes())
            .createdDate(knowledgePost.getCreatedDate())
            .build();
    }

}
