package com.innim.okkycopy.domain.board.knowledge;

import static org.assertj.core.api.Assertions.assertThat;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.dto.request.write.PostAddRequest;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgePostRepository;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class KnowledgePostRepositoryTest {

    @Autowired
    KnowledgePostRepository knowledgePostRepository;
    @Autowired
    BoardTopicRepository boardTopicRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    void findByPostIdTest() {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        PostAddRequest postAddRequest = writeRequest();
        BoardTopic boardTopic = boardTopicRepository.findByName(postAddRequest.getTopic()).get();

        List<Post> posts = new ArrayList<>();
        member.setPosts(posts);
        KnowledgePost knowledgePost = KnowledgePost.create(postAddRequest, boardTopic, member);

        entityManager.merge(member);
        entityManager.persist(knowledgePost);

        // when
        KnowledgePost post = knowledgePostRepository.findByPostId(1L).get();

        // then
        assertThat(post.getTitle()).isEqualTo(knowledgePost.getTitle());
    }

    PostAddRequest writeRequest() {
        return PostAddRequest.builder()
            .title("test_title")
            .content("test_content")
            .topic("컬럼")
            .tags(Arrays.asList())
            .build();
    }
}