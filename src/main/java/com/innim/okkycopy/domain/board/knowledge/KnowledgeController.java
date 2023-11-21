package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
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

}
