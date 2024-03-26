package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.request.ScrapRequest;
import com.innim.okkycopy.domain.board.dto.response.FileResponse;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.common.S3Uploader;
import com.innim.okkycopy.global.error.exception.ServiceException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final S3Uploader s3Uploader;

    @GetMapping("/topics")
    public ResponseEntity<Object> serveTopics() {
        return ResponseEntity.ok(boardService.findAllBoardTopics());
    }

    @PostMapping("/post/scrap")
    public ResponseEntity<Object> doScrap(
            @RequestBody ScrapRequest scrapRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.scrapPost(customUserDetails.getMember(), scrapRequest.getPostId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/scrap")
    public ResponseEntity<Object> cancelScrap(
            @RequestBody ScrapRequest scrapRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.cancelScrap(customUserDetails.getMember(), scrapRequest.getPostId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Object> makeLikeExpression(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        boardService.insertPostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/posts/{id}/hate")
    public ResponseEntity<Object> makeHateExpression(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        boardService.insertPostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/posts/{id}/like")
    public ResponseEntity<Object> removeLikeExpression(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        boardService.deletePostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/posts/{id}/hate")
    public ResponseEntity<Object> removeHateExpression(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable long id) {
        boardService.deletePostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/file/upload")
    public ResponseEntity<Object> saveFile(@RequestParam("file") MultipartFile file)
            throws ServiceException, IOException {
        return ResponseEntity.ok(new FileResponse(s3Uploader.uploadFileToS3(file)));
    }

}
