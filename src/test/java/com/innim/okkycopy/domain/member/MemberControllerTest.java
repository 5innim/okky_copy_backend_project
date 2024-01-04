package com.innim.okkycopy.domain.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;
    @InjectMocks
    private MemberController memberController;
//    MockMvc mockMvc;

//    @BeforeEach
//    void init() {
//        mockMvc = MockMvcBuilders
//            .standaloneSetup(memberController)
//            .setControllerAdvice(new GlobalExceptionHandler())
//            .build();
//    }

    @Test
    void signupTest() throws Exception {

        // given
        SignupRequest request = signupRequest();
        BriefMemberInfo response = briefMemberInfo();

        given(memberService.insertMember(any(SignupRequest.class))).willReturn(response);

        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders.post("/member/signup")
//                .characterEncoding("UTF-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new Gson().toJson(request))
//        ).andDo(print());
        memberController.signup(request);

        // then
        then(memberService).should(times(1)).insertMember(any(SignupRequest.class));
        then(memberService).shouldHaveNoMoreInteractions();
//        resultActions
//            .andExpect(status().is(201))
//            .andExpect(jsonPath("email", response.getEmail()).exists())
//            .andExpect(jsonPath("name", response.getName()).exists())
//            .andExpect(jsonPath("nickname", response.getNickname()).exists());

    }

//    @Test   => 통합 테스트로 이전되어야 할듯.
//    void given_invalidInput_then_responseErrorCode() throws Exception {
//
//        // given
//        SignupRequest request = signupRequest();
//        request.setName("testName**");
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders.post("/member/signup")
//                .characterEncoding("UTF-8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new Gson().toJson(request))
//        ).andDo(print());
//
//        // then
//        resultActions.andExpect(jsonPath("code", 400004).exists());
//    }

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

}