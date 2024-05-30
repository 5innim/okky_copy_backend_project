package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.ChangePasswordRequest;
import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.domain.member.dto.request.UpdateEmailRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.service.GoogleMemberService;
import com.innim.okkycopy.domain.member.service.KakaoMemberService;
import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.domain.member.service.MemberEmailService;
import com.innim.okkycopy.domain.member.service.NaverMemberService;
import com.innim.okkycopy.domain.member.service.OkkyMemberService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.util.EncryptionUtil;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final OkkyMemberService okkyMemberService;
    private final MemberCrudService memberCrudService;
    private final GoogleMemberService googleMemberService;
    private final KakaoMemberService kakaoMemberService;
    private final NaverMemberService naverMemberService;
    private final MemberEmailService memberEmailService;

    @PostMapping("/signup")
    public ResponseEntity<MemberBriefResponse> memberAdd(@Valid @RequestBody MemberRequest memberRequest) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(okkyMemberService.addMember(memberRequest));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> memberRemove(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberCrudService.removeMember(customUserDetails.getMember());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/info")
    public ResponseEntity<MemberDetailsResponse> memberDetails(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(memberCrudService.findMember(customUserDetails.getMember()));
    }

    @PutMapping("/profile-update")
    public ResponseEntity<Object> memberModify(@Valid @RequestBody ProfileUpdateRequest profileUpdateRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberCrudService.modifyMember(customUserDetails.getMember(), profileUpdateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> memberModify(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        okkyMemberService.modifyMember(customUserDetails.getMember(), changePasswordRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/update-email")
    public void authenticationMailSend(@Valid @RequestBody UpdateEmailRequest updateEmailRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        memberEmailService.sendAuthenticationMail(updateEmailRequest, customUserDetails.getMember());
    }

    @PutMapping("/email-authenticate")
    public ResponseEntity<Object> emailAuthenticate(String key) {
        memberCrudService.modifyMemberRole(EncryptionUtil.base64Decode(key));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/email-change-authenticate")
    public ResponseEntity<Object> emailChangeAuthenticate(String key) {
        memberCrudService.modifyMemberRoleAndEmail(EncryptionUtil.base64Decode(key));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
        memberCrudService.modifyMemberLoginDate(memberId, localDateTime);

        ResponseUtil.addCookieWithHttpOnly(response, "accessToken", JwtUtil.generateAccessToken(memberId, loginDate));
        ResponseUtil.addCookieWithHttpOnly(response, "refreshToken", JwtUtil.generateRefreshToken(memberId, loginDate));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/logout")
    public ResponseEntity<Object> memberModify(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Date logoutDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(logoutDate.toInstant(), ZoneId.systemDefault());
        memberCrudService.modifyMemberLogoutDate(customUserDetails.getMember(), localDateTime);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
