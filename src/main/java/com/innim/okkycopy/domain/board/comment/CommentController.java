package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.comment.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{id}/comment")
    public ResponseEntity<Object> writeComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid WriteCommentRequest writeCommentRequest,
            @PathVariable("id") long id) {

        commentService.saveComment(customUserDetails, writeCommentRequest, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Object> editComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid WriteCommentRequest writeCommentRequest,
            @PathVariable("id") long id) {
        commentService.updateComment(customUserDetails, writeCommentRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Object> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("id") long id) {
        commentService.deleteComment(customUserDetails, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<Object> getComments(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        return ResponseEntity.ok(commentService.selectComments(customUserDetails, id));
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/recomment")
    public ResponseEntity<Object> writeReComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long postId,
            @PathVariable long commentId,
            @RequestBody @Valid WriteReCommentRequest writeReCommentRequest) {
        commentService.saveReComment(customUserDetails, postId, commentId, writeReCommentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/comments/{id}/recomments")
    public ResponseEntity<Object> getReComments(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.selectReComments(customUserDetails, id));
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Object> makeLikeExpression(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        commentService.insertCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/comments/{id}/hate")
    public ResponseEntity<Object> makeHateExpression(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        commentService.insertCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
