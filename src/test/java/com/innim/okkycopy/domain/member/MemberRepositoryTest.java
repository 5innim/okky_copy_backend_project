package com.innim.okkycopy.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    void saveAndExistsByTest() {

        // given
        MemberRequest memberRequest = signupRequest();

        // when
        memberRepository.save(Member.from(memberRequest));

        // then
        assertThat(memberRepository.existsById("test1")).isTrue();
        assertThat(memberRepository.existsByEmail("test1@test.com")).isTrue();
    }

    @Test
    @Transactional
    void when_saveEntity_then_autoGenerateTimestamp() {
        // given
        MemberRequest memberRequest = signupRequest();

        // when
        Member member = memberRepository.save(Member.from(memberRequest));

        // then
        assertThat(member.getCreatedDate()).isNotNull();
    }

    private MemberRequest signupRequest() {
        return MemberRequest.builder()
            .id("test1")
            .password("test1234**")
            .email("test1@test.com")
            .name("testNameOne")
            .nickname("testNickname1")
            .emailCheck(true)
            .build();
    }

}