package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.ReCommentListResponse;
import com.innim.okkycopy.domain.board.comment.service.CommentCrudService;
import com.innim.okkycopy.domain.board.comment.service.CommentExpressionService;
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

    private final CommentCrudService commentCrudService;
    private final CommentExpressionService commentExpressionService;

    @PostMapping("/posts/{id}/comment")
    public ResponseEntity<Object> commentAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid CommentRequest commentRequest,
        @PathVariable("id") long id) {

        commentCrudService.addComment(customUserDetails, commentRequest, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Object> commentModify(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody @Valid CommentRequest commentRequest,
        @PathVariable("id") long id) {
        commentCrudService.modifyComment(customUserDetails, commentRequest, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Object> commentRemove(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable("id") long id) {
        commentCrudService.removeComment(customUserDetails, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<CommentListResponse> commentList(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        return ResponseEntity.ok(commentCrudService.findComments(customUserDetails, id));
    }

    @PostMapping("/comments/{commentId}/recomment")
    public ResponseEntity<Object> reCommentAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long commentId,
        @RequestBody @Valid ReCommentRequest reCommentRequest) {
        commentCrudService.addReComment(customUserDetails, commentId, reCommentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/comments/{id}/recomments")
    public ResponseEntity<ReCommentListResponse> reCommentList(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentCrudService.findReComments(customUserDetails, id));
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Object> likeExpressionAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentExpressionService.addCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/comments/{id}/hate")
    public ResponseEntity<Object> hateExpressionAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentExpressionService.addCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/comments/{id}/like")
    public ResponseEntity<Object> likeExpressionRemove(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentExpressionService.removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{id}/hate")
    public ResponseEntity<Object> hateExpressionRemove(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        commentExpressionService.removeCommentExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
