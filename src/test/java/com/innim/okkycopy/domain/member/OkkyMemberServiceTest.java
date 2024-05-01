//package com.innim.okkycopy.domain.member;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.Mockito.times;
//
//import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
//import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
//import com.innim.okkycopy.domain.member.entity.Member;
//import com.innim.okkycopy.domain.member.repository.MemberRepository;
//import com.innim.okkycopy.domain.member.service.OkkyMemberService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@ExtendWith(MockitoExtension.class)
//class OkkyMemberServiceTest {
//
//    @Mock
//    MemberRepository memberRepository;
//    @Mock
//    PasswordEncoder passwordEncoder;
//    @InjectMocks
//    OkkyMemberService okkyMemberService;
//
//    @Test
//    void insertMemberTest() {
//        // given
//        MemberRequest memberRequest = signupRequest();
//        given(memberRepository.save(any(Member.class))).willReturn(null);
//        given(passwordEncoder.encode(any(String.class))).willReturn("**************");
//
//        // when
//        MemberBriefResponse briefMemberInfo = okkyMemberService.addMember(memberRequest);
//
//        // then
//        then(memberRepository).should(times(1)).save(any(Member.class));
//        then(memberRepository).should(times(1)).existsById(any(String.class));
//        then(memberRepository).should(times(1)).existsByEmail(any(String.class));
//        then(memberRepository).shouldHaveNoMoreInteractions();
//
//        assertThat(briefMemberInfo.getEmail()).isEqualTo(memberRequest.getEmail());
//        assertThat(briefMemberInfo.getName()).isEqualTo(memberRequest.getName());
//        assertThat(briefMemberInfo.getNickname()).isEqualTo(memberRequest.getNickname());
//
//    }
//
//    private MemberRequest signupRequest() {
//        return MemberRequest.builder()
//            .id("test1")
//            .password("test1234**")
//            .email("test1@test.com")
//            .name("testNameOne")
//            .nickname("testNickname1")
//            .emailCheck(true)
//            .build();
//    }
//}