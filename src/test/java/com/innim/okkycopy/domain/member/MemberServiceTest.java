package com.innim.okkycopy.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberInfo;
import com.innim.okkycopy.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    MemberService memberService;

//    @Autowired
//    MemberService memberServiceBean;

    @Test
    void insertMemberTest() {

        // given
        SignupRequest signupRequest = signupRequest();
        given(memberRepository.save(any(Member.class))).willReturn(null);

        // when
        BriefMemberInfo briefMemberInfo = memberService.insertMember(signupRequest);

        // then
        then(memberRepository).should(times(1)).save(any(Member.class));
        then(memberRepository).should(times(1)).existsById(any(String.class));
        then(memberRepository).should(times(1)).existsByEmail(any(String.class));
        then(memberRepository).shouldHaveNoMoreInteractions();

        assertThat(briefMemberInfo.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(briefMemberInfo.getName()).isEqualTo(signupRequest.getName());
        assertThat(briefMemberInfo.getNickname()).isEqualTo(signupRequest.getNickname());

    }

    @Test
    void given_samePassword_then_returnDifferentEncodedPassword() {

        // given
        SignupRequest signupRequest1 = signupRequest();
        SignupRequest signupRequest2 = signupRequest();
        passwordEncoder = new BCryptPasswordEncoder();

        // when
        signupRequest1.encodePassword(passwordEncoder);
        signupRequest2.encodePassword(passwordEncoder);

        // then
        assertThat(signupRequest1.getPassword()).isNotEqualTo(signupRequest2.getPassword());
    }

//    @Test
//    @Transactional   => 중복 체크로 select 쿼리 실행. 따라서 통합 테스트에 재작성 필요
//    void given_duplicatedIdOrEmail_then_duplicateException() {
//
//        // given
//        SignupRequest signupRequest1 = signupRequest();
//        SignupRequest signupRequest2 = signupRequest();
//
//        // when
//        memberServiceBean.insertMember(signupRequest1);
//
//        // then
//        assertThrows(DuplicateException.class, () -> {
//            memberServiceBean.insertMember(signupRequest2);
//        });
//    }

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