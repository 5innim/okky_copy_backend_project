package com.innim.okkycopy.domain.board.comment.dto.response;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
public class CommentListResponse {

    private List<CommentDetailsResponse> comments;

    public static CommentListResponse of(
        List<Comment> comments,
        List<Short> expressionTypes,
        Member requester) {

        List<CommentDetailsResponse> commentResponses = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            RequesterInfo requesterInfo =
                (requester == null) ? null : RequesterInfo.builder()
                    .like(
                        expressionTypes.get(i) != null && Integer.valueOf(expressionTypes.get(i))
                            .equals(ExpressionType.LIKE.getValue()))
                    .hate(
                        expressionTypes.get(i) != null && Integer.valueOf(expressionTypes.get(i))
                            .equals(ExpressionType.HATE.getValue()))
                    .build();

            commentResponses.add(
                CommentDetailsResponse.of(
                    comments.get(i),
                    requesterInfo
                )
            );
        }

        return new CommentListResponse(commentResponses);
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class CommentDetailsResponse {

        private Long commentId;
        private WriterInfo writerInfo;
        private String content;
        private LocalDateTime createdDate;
        private LocalDateTime lastUpdate;
        private Integer likes;
        private Integer hates;
        private RequesterInfo requesterInfo;

        public static CommentDetailsResponse of(
            Comment comment,
            RequesterInfo requesterInfo) {
            return CommentDetailsResponse.builder()
                .writerInfo((comment.getMember() == null)
                    ? null : WriterInfo.from(comment.getMember()))
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .lastUpdate(comment.getLastUpdate())
                .likes(comment.getLikes())
                .hates(comment.getHates())
                .commentId(comment.getCommentId())
                .requesterInfo(requesterInfo)
                .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class RequesterInfo {

        private boolean like;
        private boolean hate;
    }
}
