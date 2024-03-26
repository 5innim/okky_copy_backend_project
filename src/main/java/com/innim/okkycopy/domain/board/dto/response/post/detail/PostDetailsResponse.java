package com.innim.okkycopy.domain.board.dto.response.post.detail;

import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PostDetailsResponse {

    private WriterInfo writerInfo;
    private String title;
    private String content;
    private List<String> tags;
    private Integer views;
    private Integer likes;
    private Integer hates;
    private LocalDateTime createdDate;
    private RequesterInfo requesterInfo;


    public static PostDetailsResponse create(KnowledgePost knowledgePost, Member member) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : knowledgePost.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailsResponse.builder()
            .writerInfo((member == null) ? null : WriterInfo.builder()
                .memberId(member.getMemberId())
                .nickName(member.getNickname())
                .profile(member.getProfile()).build())
            .title(knowledgePost.getTitle())
            .content(knowledgePost.getContent())
            .tags(tags).views(knowledgePost.getViews())
            .likes(knowledgePost.getLikes())
            .hates(knowledgePost.getHates())
            .createdDate(knowledgePost.getCreatedDate())
            .build();
    }

}
