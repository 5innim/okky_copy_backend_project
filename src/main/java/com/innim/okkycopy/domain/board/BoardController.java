package com.innim.okkycopy.domain.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/board/topics")
    public ResponseEntity serveTopics (HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(boardService.findAllBoardTypes());
    }
}
