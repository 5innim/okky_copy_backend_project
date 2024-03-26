package com.innim.okkycopy.domain.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.innim.okkycopy.domain.board.entity.Post;
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
        // init_test_data procedure success

        // when
        Post savedPost = postRepository.findByPostId(1L).get();

        // then
        assertThat(savedPost.getPostId()).isEqualTo(1);
    }

}