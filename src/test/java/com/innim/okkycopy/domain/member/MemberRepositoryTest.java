package com.innim.okkycopy.domain.member;

import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@TestPropertySource(locations = "classpath:application-dev.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    void saveAndExistsByTest() {

        // given
        SignupRequest signupRequest = signupRequest();

        // when
        memberRepository.save(Member.toMemberEntity(signupRequest));

        // then
        assertThat(memberRepository.existsById("test1")).isTrue();
        assertThat(memberRepository.existsByEmail("test1@test.com")).isTrue();
    }

    @Test
    @Transactional
    void when_saveEntity_then_autoGenerateTimestamp() {
        // given
        SignupRequest signupRequest = signupRequest();

        // when
        Member member = memberRepository.save(Member.toMemberEntity(signupRequest));

        // then
        assertThat(member.getCreatedDate()).isNotNull();
    }

    private SignupRequest signupRequest() {
        return SignupRequest.builder()
            .id("test1")
            .password("test1234**")
            .email("test1@test.com")
            .name("testNameOne")
            .nickname("testNickname1")
            .emailCheck(true)
            .build();
    }

}