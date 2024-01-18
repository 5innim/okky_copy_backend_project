package com.innim.okkycopy.domain.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.domain.board.entity.Post;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    void findByPostIdTest() {
        // given
        Post post = post();
        postRepository.save(post);

        // when
        Post savedPost = postRepository.findByPostId(1l).get();

        // then
        assertThat(post.getPostId()).isEqualTo(savedPost.getPostId());
    }

    Post post() {
        return Post.builder()
            .postId(1l)
            .title("test_post")
            .tags(Arrays.asList())
            .createdDate(LocalDateTime.now())
            .content("test_content")
            .lastUpdate(LocalDateTime.now())
            .scrapList(Arrays.asList())
            .build();
    }
}