package com.innim.okkycopy.domain.board.community;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostService communityPostService;

    @PostMapping("/write")
    public ResponseEntity<Object> communityPostAdd(@RequestBody @Valid PostRequest postRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        communityPostService.addCommunityPost(postRequest, customUserDetails);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Object> communityPostDetails(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        return ResponseEntity.ok(communityPostService.findCommunityPost(customUserDetails, id));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Object> communityPostModify(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid PostRequest updateRequest,
        @PathVariable("id") long id) {
        communityPostService.modifyCommunityPost(customUserDetails, updateRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
