package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
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
@RequestMapping("/board/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping("/write")
    public ResponseEntity<Object> writeKnowledgePost(@RequestBody @Valid WriteRequest writeRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        knowledgeService.saveKnowledgePost(writeRequest, customUserDetails);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Object> getKnowledgePost(@PathVariable("id") long id) {
        return ResponseEntity.ok(knowledgeService.selectKnowledgePost(id));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Object> editKnowledgePost(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid WriteRequest updateRequest,
        @PathVariable("id") long id) {
        knowledgeService.updateKnowledgePost(customUserDetails, updateRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Object> deleteKnowledgePost(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        knowledgeService.deleteKnowledgePost(customUserDetails, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/posts/{id}/comment")
    public ResponseEntity<Object> writeKnowledgeComment(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid WriteCommentRequest writeCommentRequest,
        @PathVariable("id") long id) {
        knowledgeService.saveKnowledgeComment(customUserDetails, writeCommentRequest, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Object> editKnowledgeComment(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid WriteCommentRequest writeCommentRequest,
        @PathVariable("id") long id) {
        knowledgeService.updateKnowledgeComment(customUserDetails, writeCommentRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Object> deleteKnowledgeComment(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        knowledgeService.deleteKnowledgeComment(customUserDetails, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<Object> getKnowledgePostComments(@PathVariable long id) {
        return ResponseEntity.ok(knowledgeService.selectKnowledgeComments(id));
    }

}
