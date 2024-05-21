package com.innim.okkycopy.domain.board.dto.response.post.detail;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
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
    private String topicName;
    private String content;
    private List<String> tags;
    private Integer views;
    private Integer likes;
    private Integer hates;
    private LocalDateTime createdDate;
    private RequesterInfo requesterInfo;


    // TODO: should add case for another post domain at this method
    public static PostDetailsResponse of(KnowledgePost knowledgePost, Member member) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : knowledgePost.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailsResponse.builder()
            .writerInfo((member == null) ? null : WriterInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(member.getProfile()).build())
            .title(knowledgePost.getTitle())
            .topicName(knowledgePost.getBoardTopic().getName())
            .content(knowledgePost.getContent())
            .tags(tags).views(knowledgePost.getViews())
            .likes(knowledgePost.getLikes())
            .hates(knowledgePost.getHates())
            .createdDate(knowledgePost.getCreatedDate())
            .build();
    }

    public static PostDetailsResponse of(CommunityPost communityPost, Member member) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : communityPost.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailsResponse.builder()
            .writerInfo((member == null) ? null : WriterInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(member.getProfile()).build())
            .title(communityPost.getTitle())
            .topicName(communityPost.getBoardTopic().getName())
            .content(communityPost.getContent())
            .tags(tags).views(communityPost.getViews())
            .likes(communityPost.getLikes())
            .hates(communityPost.getHates())
            .createdDate(communityPost.getCreatedDate())
            .build();
    }

    public static PostDetailsResponse of(EventPost eventPost, Member member) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : eventPost.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailsResponse.builder()
            .writerInfo((member == null) ? null : WriterInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(member.getProfile()).build())
            .title(eventPost.getTitle())
            .topicName(eventPost.getBoardTopic().getName())
            .content(eventPost.getContent())
            .tags(tags).views(eventPost.getViews())
            .likes(eventPost.getLikes())
            .hates(eventPost.getHates())
            .createdDate(eventPost.getCreatedDate())
            .build();
    }

    public static PostDetailsResponse of(QnaPost qnaPost, Member member) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : qnaPost.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailsResponse.builder()
            .writerInfo((member == null) ? null : WriterInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(member.getProfile()).build())
            .title(qnaPost.getTitle())
            .topicName(qnaPost.getBoardTopic().getName())
            .content(qnaPost.getContent())
            .tags(tags).views(qnaPost.getViews())
            .likes(qnaPost.getLikes())
            .hates(qnaPost.getHates())
            .createdDate(qnaPost.getCreatedDate())
            .build();
    }

}
