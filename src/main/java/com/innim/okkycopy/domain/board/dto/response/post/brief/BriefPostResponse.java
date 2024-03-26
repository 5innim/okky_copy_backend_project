package com.innim.okkycopy.domain.board.dto.response.post.brief;

import com.innim.okkycopy.domain.board.dto.response.post.detail.WriterInfoResponse;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
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
public class BriefPostResponse {

    long postId;
    String title;
    WriterInfoResponse writerInfo;
    LocalDateTime createdDate;
    List<String> tags;
    int views;
    int comments;
    int likes;

    public static <T extends Post> BriefPostResponse createBriefPostResponse(T post) {
        List<String> tags = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            tags.add(tag.getName());
        }

        BriefPostResponse response = BriefPostResponse.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .writerInfo(WriterInfoResponse.toWriterInfoRequestDto(post.getMember()))
            .createdDate(post.getCreatedDate())
            .tags(tags)
            .build();

        if (post instanceof KnowledgePost) {
            response.setViews(((KnowledgePost) post).getViews());
            response.setComments(((KnowledgePost) post).getComments());
            response.setLikes(((KnowledgePost) post).getLikes());
        }

        return response;
    }

}
