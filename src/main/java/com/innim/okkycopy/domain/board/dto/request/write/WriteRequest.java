package com.innim.okkycopy.domain.board.dto.request.write;

import com.innim.okkycopy.global.common.validation.annotation.ListSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteRequest {

    @Size(min = 1)
    private String topic;
    @Size(min = 1, max = 30)
    private String title;
    @ListSize(max = 3)
    private List<@Valid TagRequest> tags;
    @Size(min = 1, max = 20000)
    private String content;
}
