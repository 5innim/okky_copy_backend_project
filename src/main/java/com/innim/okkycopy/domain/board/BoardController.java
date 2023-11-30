package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.request.ScrapRequest;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/topics")
    public ResponseEntity serveTopics(HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(boardService.findAllBoardTopics());
    }

    @PostMapping("/post/scrap")
    public ResponseEntity doScrap(@RequestBody ScrapRequest scrapRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.scrapPost(customUserDetails.getMember(), scrapRequest.getPostId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/scrap")
    public ResponseEntity cancelScrap(@RequestBody ScrapRequest scrapRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.cancelScrap(customUserDetails.getMember(), scrapRequest.getPostId());
        return ResponseEntity.ok().build();
    }

}
