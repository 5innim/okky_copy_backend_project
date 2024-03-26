package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
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
@RequiredArgsConstructor
@RequestMapping("/board")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{id}/comment")
    public ResponseEntity<Object> commentAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid CommentRequest commentRequest,
        @PathVariable("id") long id) {

        commentService.addComment(customUserDetails, commentRequest, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Object> commentModify(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid CommentRequest commentRequest,
        @PathVariable("id") long id) {
        commentService.modifyComment(customUserDetails, commentRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Object> commentRemove(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        commentService.removeComment(customUserDetails, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<Object> commentList(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        return ResponseEntity.ok(commentService.findComments(customUserDetails, id));
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/recomment")
    public ResponseEntity<Object> reCommentAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long postId,
        @PathVariable long commentId,
        @RequestBody @Valid ReCommentRequest reCommentRequest) {
        commentService.addReComment(customUserDetails, postId, commentId, reCommentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/comments/{id}/recomments")
    public ResponseEntity<Object> reCommentList(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findReComments(customUserDetails, id));
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Object> likeExpressionAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentService.addCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/comments/{id}/hate")
    public ResponseEntity<Object> hateExpressionAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentService.addCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/comments/{id}/like")
    public ResponseEntity<Object> likeExpressionRemove(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentService.removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{id}/hate")
    public ResponseEntity<Object> hateExpressionRemove(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentService.removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
