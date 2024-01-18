package com.innim.okkycopy.domain.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.board.BoardService;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ScrapRepositoryTest {
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    void findByMemberAndPostTest() {
        // given
        Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
        Post post = post();
        memberRepository.save(member);
        postRepository.save(post);

        Scrap scrap = Scrap.builder()
            .post(post)
            .member(member)
            .build();
        entityManager.persist(scrap);

        // when
        Scrap savedScrap = scrapRepository.findByMemberAndPost(post, member).get();

        // then
        assertThat(savedScrap).isNotNull();
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