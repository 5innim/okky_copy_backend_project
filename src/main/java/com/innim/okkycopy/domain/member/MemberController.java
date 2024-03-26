package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberResponse;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<BriefMemberResponse> memberAdd(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(memberService.addMember(signupRequest));
    }

    @GetMapping("/info")
    public ResponseEntity<MemberResponse> memberDetails(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(memberService.findMember(customUserDetails.getMember()));
    }


}
