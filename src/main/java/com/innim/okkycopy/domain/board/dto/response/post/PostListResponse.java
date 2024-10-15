package com.innim.okkycopy.domain.board.dto.response.post;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostListResponse {

    private List<PostBriefResponse> posts;
    private int totalPages;

    public static <T extends Post>  PostListResponse from(Page<T> postsPage) {
        ArrayList<PostBriefResponse> briefPosts = new ArrayList<>();
        for (T post : postsPage.getContent()) {
            briefPosts.add(PostBriefResponse.from(post));
        }

        return PostListResponse.builder()
            .totalPages(postsPage.getTotalPages())
            .posts(briefPosts)
            .build();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class PostBriefResponse {

        private long postId;
        private String title;
        private WriterInfo writerInfo;
        private LocalDateTime createdDate;
        private List<String> tags;
        private int views;
        private int comments;
        private int likes;

        public static PostBriefResponse from(Post post) {
            List<String> tags = new ArrayList<>();
            for (Tag tag : post.getTags()) {
                tags.add(tag.getName());
            }

            return PostBriefResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .writerInfo((post.getMember() == null) ? null : WriterInfo.from(post.getMember()))
                .createdDate(post.getCreatedDate())
                .tags(tags)
                .views(post.getViews())
                .comments(post.getComments())
                .likes(post.getLikes())
                .build();
        }
    }
}
