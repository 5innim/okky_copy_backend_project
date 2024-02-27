package com.innim.okkycopy.domain.board.dto.response.post.brief;

import com.innim.okkycopy.domain.board.entity.Post;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponse {
    List<BriefPostResponse> posts;
    int totalPages;
    public static <T extends Post> PostsResponse createPostsResponse(Page<T> postsPage) {
        ArrayList<BriefPostResponse> briefPosts = new ArrayList<>();
        for (T post : postsPage.getContent()) {
            briefPosts.add(BriefPostResponse.createBriefPostResponse(post));
        }

        return PostsResponse.builder()
                .totalPages(postsPage.getTotalPages())
                .posts(briefPosts)
                .build();
    }
}
