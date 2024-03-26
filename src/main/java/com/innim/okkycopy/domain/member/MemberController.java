package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.MemberAddRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
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
    public ResponseEntity<MemberBriefResponse> memberAdd(@Valid @RequestBody MemberAddRequest memberAddRequest) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(memberService.addMember(memberAddRequest));
    }

    @GetMapping("/info")
    public ResponseEntity<MemberDetailsResponse> memberDetails(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(memberService.findMember(customUserDetails.getMember()));
    }


}
