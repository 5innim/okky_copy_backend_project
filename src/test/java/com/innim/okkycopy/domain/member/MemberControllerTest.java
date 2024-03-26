package com.innim.okkycopy.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;
    @InjectMocks
    private MemberController memberController;

    @Test
    void signupTest() {
        // given
        SignupRequest request = signupRequest();
        BriefMemberResponse briefMemberInfo = briefMemberInfo();

        given(memberService.addMember(any(SignupRequest.class))).willReturn(briefMemberInfo);

        // when
        ResponseEntity response = memberController.memberAdd(request);

        // then
        then(memberService).should(times(1)).addMember(any(SignupRequest.class));
        then(memberService).shouldHaveNoMoreInteractions();
        assertThat(response.getBody()).isInstanceOf(BriefMemberResponse.class).isEqualTo(briefMemberInfo);
    }

    @Test
    void serveMemberInfo() {
        // given
        CustomUserDetails request = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        MemberResponse memberInfo = memberInfo();

        given(memberService.findMember(any(Member.class))).willReturn(memberInfo);

        // when
        ResponseEntity response = memberController.memberDetails(request);

        // then
        then(memberService).should(times(1)).findMember(any(Member.class));
        then(memberService).shouldHaveNoMoreInteractions();
        assertThat(response.getBody()).isInstanceOf(MemberResponse.class).isEqualTo(memberInfo);
    }

    private SignupRequest signupRequest() {
        return SignupRequest.builder()
            .id("test1234")
            .password("test1234**")
            .email("test@test.com")
            .name("testName")
            .nickname("testNickname")
            .emailCheck(true)
            .build();
    }

    private BriefMemberResponse briefMemberInfo() {
        return BriefMemberResponse.builder()
            .name("testName")
            .nickname("testNickname")
            .email("test@test.com")
            .build();
    }

    private MemberResponse memberInfo() {
        return MemberResponse.builder()
            .memberId(1L)
            .nickname("testNickname")
            .scrappedPost(new ArrayList<>())
            .build();
    }

}