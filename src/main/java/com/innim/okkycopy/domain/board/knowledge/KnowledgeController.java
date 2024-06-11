package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgePostService knowledgePostService;

    @PostMapping("/write")
    public ResponseEntity<Object> knowledgePostAdd(@RequestBody @Valid PostRequest postRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        knowledgePostService.addKnowledgePost(postRequest, customUserDetails);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Object> knowledgePostDetails(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        return ResponseEntity.ok(knowledgePostService.findKnowledgePost(customUserDetails, id));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Object> knowledgePostModify(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid PostRequest updateRequest,
        @PathVariable("id") long id) {
        knowledgePostService.modifyKnowledgePost(customUserDetails, updateRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Object> knowledgePostRemove(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        knowledgePostService.removeKnowledgePost(customUserDetails, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> briefPostList(
        @RequestParam(required = false) Long topicId,
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok()
            .body(knowledgePostService.findKnowledgePostsByTopicIdAndKeyword(topicId, keyword, pageable));
    }

}
