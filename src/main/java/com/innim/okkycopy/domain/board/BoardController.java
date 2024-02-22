package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.request.ScrapRequest;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/topics")
    public ResponseEntity<Object> serveTopics() {
        return ResponseEntity.ok(boardService.findAllBoardTopics());
    }

    @PostMapping("/post/scrap")
    public ResponseEntity<Object> doScrap(@RequestBody ScrapRequest scrapRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.scrapPost(customUserDetails.getMember(), scrapRequest.getPostId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/scrap")
    public ResponseEntity<Object> cancelScrap(@RequestBody ScrapRequest scrapRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.cancelScrap(customUserDetails.getMember(), scrapRequest.getPostId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Object> makeLikeExpression(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long id) {
        boardService.insertPostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{id}/hate")
    public ResponseEntity<Object> makeHateExpression(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long id) {
        boardService.insertPostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.ok().build();
    }



}
