package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.response.FileResponse;
import com.innim.okkycopy.domain.board.dto.response.TopTagListResponse;
import com.innim.okkycopy.domain.board.dto.response.TopicListResponse;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.service.BoardTopicService;
import com.innim.okkycopy.domain.board.service.PostExpressionService;
import com.innim.okkycopy.domain.board.service.PostScrapService;
import com.innim.okkycopy.domain.board.service.TopTagService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.common.storage.S3Uploader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final PostExpressionService postExpressionService;
    private final PostScrapService postScrapService;
    private final BoardTopicService boardTopicService;
    private final TopTagService topTagService;
    private final S3Uploader s3Uploader;

    @GetMapping("/topics")
    public ResponseEntity<TopicListResponse> boardTopicList() {
        return ResponseEntity.ok(boardTopicService.findBoardTopics());
    }

    @GetMapping("/top-tag-list")
    public ResponseEntity<TopTagListResponse> topTagList() {
        return ResponseEntity.ok(topTagService.findAll());
    }

    @PostMapping("/posts/{id}/scrap")
    public ResponseEntity<Object> scrapAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long id) {
        postScrapService.addScrap(customUserDetails.getMember(), id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/posts/{id}/scrap")
    public ResponseEntity<Object> scrapRemove(
        @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long id) {
        postScrapService.removeScrap(customUserDetails.getMember(), id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Object> likeExpressionAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        postExpressionService.addPostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/posts/{id}/hate")
    public ResponseEntity<Object> hateExpressionAdd(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        postExpressionService.addPostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/posts/{id}/like")
    public ResponseEntity<Object> likeExpressionRemove(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        postExpressionService.removePostExpression(customUserDetails.getMember(), id, ExpressionType.LIKE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/posts/{id}/hate")
    public ResponseEntity<Object> hateExpressionRemove(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @PathVariable long id) {
        postExpressionService.removePostExpression(customUserDetails.getMember(), id, ExpressionType.HATE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/file/upload")
    public ResponseEntity<FileResponse> fileAdd(@RequestParam("file") MultipartFile file)
        throws IOException {
        return ResponseEntity.ok(new FileResponse(s3Uploader.uploadFileToS3(file)));
    }

}
