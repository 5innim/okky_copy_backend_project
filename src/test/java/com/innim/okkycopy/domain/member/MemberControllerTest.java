package com.innim.okkycopy.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.dto.request.MemberAddRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
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
        MemberAddRequest request = signupRequest();
        MemberBriefResponse briefMemberInfo = briefMemberInfo();

        given(memberService.addMember(any(MemberAddRequest.class))).willReturn(briefMemberInfo);

        // when
        ResponseEntity response = memberController.memberAdd(request);

        // then
        then(memberService).should(times(1)).addMember(any(MemberAddRequest.class));
        then(memberService).shouldHaveNoMoreInteractions();
        assertThat(response.getBody()).isInstanceOf(MemberBriefResponse.class).isEqualTo(briefMemberInfo);
    }

    @Test
    void serveMemberInfo() {
        // given
        CustomUserDetails request = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
        MemberDetailsResponse memberInfo = memberInfo();

        given(memberService.findMember(any(Member.class))).willReturn(memberInfo);

        // when
        ResponseEntity response = memberController.memberDetails(request);

        // then
        then(memberService).should(times(1)).findMember(any(Member.class));
        then(memberService).shouldHaveNoMoreInteractions();
        assertThat(response.getBody()).isInstanceOf(MemberDetailsResponse.class).isEqualTo(memberInfo);
    }

    private MemberAddRequest signupRequest() {
        return MemberAddRequest.builder()
            .id("test1234")
            .password("test1234**")
            .email("test@test.com")
            .name("testName")
            .nickname("testNickname")
            .emailCheck(true)
            .build();
    }

    private MemberBriefResponse briefMemberInfo() {
        return MemberBriefResponse.builder()
            .name("testName")
            .nickname("testNickname")
            .email("test@test.com")
            .build();
    }

    private MemberDetailsResponse memberInfo() {
        return MemberDetailsResponse.builder()
            .memberId(1L)
            .nickname("testNickname")
            .scrappedPost(new ArrayList<>())
            .build();
    }

}