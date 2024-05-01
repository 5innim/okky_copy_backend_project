package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.domain.member.service.OkkyMemberService;
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

    private final OkkyMemberService okkyMemberService;
    private final MemberCrudService memberCrudService;

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


}
