package com.innim.okkycopy.domain.board.repository;

import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        // init_test_data procedure success
        Post post = postRepository.findByPostId(1l).get();
        Member member = memberRepository.findByMemberId(1l).get();

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
}