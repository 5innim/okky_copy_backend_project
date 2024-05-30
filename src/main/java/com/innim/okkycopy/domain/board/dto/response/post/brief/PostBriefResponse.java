package com.innim.okkycopy.domain.board.dto.response.post.brief;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBriefResponse {

    long postId;
    String title;
    WriterInfo writerInfo;
    LocalDateTime createdDate;
    List<String> tags;
    int views;
    int comments;
    int likes;

    public static <T extends Post> PostBriefResponse from(T post) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            tags.add(tag.getName());
        }

        PostBriefResponse response = PostBriefResponse.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .writerInfo((post.getMember() == null) ? null : WriterInfo.from(post.getMember()))
            .createdDate(post.getCreatedDate())
            .tags(tags)
            .build();

        //TODO: need to expand when adding new domain of board
        if (post instanceof KnowledgePost) {
            response.setViews(((KnowledgePost) post).getViews());
            response.setComments(((KnowledgePost) post).getComments());
            response.setLikes(((KnowledgePost) post).getLikes());
        } else if (post instanceof CommunityPost) {
            response.setViews(((CommunityPost) post).getViews());
            response.setComments(((CommunityPost) post).getComments());
            response.setLikes(((CommunityPost) post).getLikes());
        } else if (post instanceof EventPost) {
            response.setViews(((EventPost) post).getViews());
            response.setComments(((EventPost) post).getComments());
            response.setLikes(((EventPost) post).getLikes());
        } else if (post instanceof QnaPost) {
            response.setViews(((QnaPost) post).getViews());
            response.setComments(((QnaPost) post).getComments());
            response.setLikes(((QnaPost) post).getLikes());
        }

        return response;
    }

}
