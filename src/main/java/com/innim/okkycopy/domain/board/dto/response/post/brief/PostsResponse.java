package com.innim.okkycopy.domain.board.dto.response.post.brief;

import com.innim.okkycopy.domain.board.entity.Post;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

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
