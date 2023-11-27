package com.innim.okkycopy.domain.member;

import static org.junit.jupiter.api.Assertions.*;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
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
        assertTrue(memberRepository.existsById("test1"));
        assertTrue(memberRepository.existsByEmail("test1@test.com"));
    }

    @Test
    @Transactional
    void when_saveEntity_then_autoGenerateTimestamp() {
        // given
        SignupRequest signupRequest = signupRequest();

        // when
        Member member = memberRepository.save(Member.toMemberEntity(signupRequest));

        // then
        assertFalse(member.getCreatedDate() == null);
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