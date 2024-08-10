package com.innim.okkycopy.domain.board.dto.response.post.brief;

import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
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

        response.setViews(post.getViews());
        response.setComments(post.getComments());
        response.setLikes(post.getLikes());

        return response;
    }

}
