package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.service.GoogleMemberService;
import com.innim.okkycopy.domain.member.service.KakaoMemberService;
import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.domain.member.service.MemberLoginService;
import com.innim.okkycopy.domain.member.service.NaverMemberService;
import com.innim.okkycopy.domain.member.service.OkkyMemberService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.util.JwtUtil;
import com.innim.okkycopy.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final OkkyMemberService okkyMemberService;
    private final MemberCrudService memberCrudService;
    private final MemberLoginService memberLoginService;
    private final GoogleMemberService googleMemberService;
    private final KakaoMemberService kakaoMemberService;
    private final NaverMemberService naverMemberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberBriefResponse> memberAdd(@Valid @RequestBody MemberRequest memberRequest) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(okkyMemberService.addMember(memberRequest));
    }

    @GetMapping("/info")
    public ResponseEntity<MemberDetailsResponse> memberDetails(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(memberCrudService.findMember(customUserDetails.getMember()));
    }

    //TODO "Adjust interceptor later"
    @PostMapping("/{provider}/signup")
    public ResponseEntity<Object> memberAdd(@Valid @RequestBody OAuthMemberRequest oAuthMemberRequest,
        @PathVariable String provider, HttpServletRequest request, HttpServletResponse response) throws IOException {

        Long memberId = null;
        switch (provider) {
            case "google":
                memberId = googleMemberService.addGoogleMember(oAuthMemberRequest, provider, request);
                break;
            case "kakao":
                memberId = kakaoMemberService.addKakaoMember(oAuthMemberRequest, provider, request);
                break;
            case "naver":
                memberId = naverMemberService.addNaverMember(oAuthMemberRequest, provider, request);
                break;
            default:
                throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
        }

        Date loginDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
        memberLoginService.modifyMemberLoginDate(memberId, localDateTime);

        ResponseUtil.addCookieWithHttpOnly(response, "accessToken", JwtUtil.generateAccessToken(memberId, loginDate));
        ResponseUtil.addCookieWithHttpOnly(response, "refreshToken", JwtUtil.generateRefreshToken(memberId, loginDate));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
