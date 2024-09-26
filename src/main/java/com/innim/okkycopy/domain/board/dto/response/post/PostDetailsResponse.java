package com.innim.okkycopy.domain.board.dto.response.post;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
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


    public static PostDetailsResponse from(Post post) {
        Member member = post.getMember();

        List<String> tags = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            tags.add(tag.getName());
        }

        return PostDetailsResponse.builder()
            .writerInfo((member == null) ? null : WriterInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profile(member.getProfile()).build())
            .title(post.getTitle())
            .topicName(post.getBoardTopic().getName())
            .content(post.getContent())
            .tags(tags).views(post.getViews())
            .likes(post.getLikes())
            .hates(post.getHates())
            .createdDate(post.getCreatedDate())
            .build();
    }

    public static PostDetailsResponse from(Post post, PostExpression expression, boolean scrap) {
        PostDetailsResponse response = from(post);
        response.setRequesterInfo(
            RequesterInfo.builder()
                .scrap(scrap)
                .like(expression != null && expression.getExpressionType().equals(ExpressionType.LIKE))
                .hate(expression != null && expression.getExpressionType().equals(ExpressionType.HATE))
                .build());

        return response;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    static class RequesterInfo {

        private boolean scrap;
        private boolean like;
        private boolean hate;
    }

}
