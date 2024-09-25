package com.innim.okkycopy.domain.board.comment.dto.response;

import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse.CommentDetailsResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse.RequesterInfo;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
public class ReCommentListResponse {

    private List<ReCommentDetailsResponse> comments;

    public static ReCommentListResponse of(
        List<Comment> comments,
        List<String> mentionedNicknames,
        List<Short> expressionTypes,
        Member requester) {

        List<ReCommentDetailsResponse> commentResponses = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            RequesterInfo requesterInfo =
                (requester == null) ? null : RequesterInfo.builder()
                    .like(
                        expressionTypes.get(i) != null && Objects.equals(Integer.valueOf(expressionTypes.get(i)),
                            ExpressionType.LIKE.getValue()))
                    .hate(
                        expressionTypes.get(i) != null && Objects.equals(Integer.valueOf(expressionTypes.get(i)),
                            ExpressionType.HATE.getValue()))
                    .build();

            commentResponses.add(
                ReCommentDetailsResponse.of(comments.get(i), mentionedNicknames.get(i), requesterInfo)
            );
        }

        return new ReCommentListResponse(commentResponses);
    }

    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    static class ReCommentDetailsResponse extends CommentDetailsResponse {

        private String mentionedNickname;

        public static ReCommentDetailsResponse of(
            Comment comment,
            String mentionedNickname,
            RequesterInfo requesterInfo) {
            return ReCommentDetailsResponse.builder()
                .writerInfo((comment.getMember() == null)
                    ? null : WriterInfo.from(comment.getMember()))
                .mentionedNickname(mentionedNickname)
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
}
