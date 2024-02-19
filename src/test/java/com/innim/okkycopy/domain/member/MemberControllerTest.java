package com.innim.okkycopy.domain.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberInfo;
import com.innim.okkycopy.domain.member.dto.response.MemberInfo;
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
        BriefMemberInfo briefMemberInfo = briefMemberInfo();

        given(memberService.insertMember(any(SignupRequest.class))).willReturn(briefMemberInfo);

        // when
        ResponseEntity response = memberController.signup(request);

        // then
        then(memberService).should(times(1)).insertMember(any(SignupRequest.class));
        then(memberService).shouldHaveNoMoreInteractions();
        assertThat(response.getBody()).isInstanceOf(BriefMemberInfo.class).isEqualTo(briefMemberInfo);
    }

    @Test
    void serveMemberInfo() {
        // given
        CustomUserDetails request = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        MemberInfo memberInfo = memberInfo();

        given(memberService.selectMember(any(Member.class))).willReturn(memberInfo);

        // when
        ResponseEntity response = memberController.serveMemberInfo(request);

        // then
        then(memberService).should(times(1)).selectMember(any(Member.class));
        then(memberService).shouldHaveNoMoreInteractions();
        assertThat(response.getBody()).isInstanceOf(MemberInfo.class).isEqualTo(memberInfo);
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

    private BriefMemberInfo briefMemberInfo() {
        return BriefMemberInfo.builder()
            .name("testName")
            .nickname("testNickname")
            .email("test@test.com")
            .build();
    }

    private MemberInfo memberInfo() {
        return MemberInfo.builder()
            .memberId(1l)
            .nickname("testNickname")
            .scrappedPost(new ArrayList<>())
            .build();
    }

}