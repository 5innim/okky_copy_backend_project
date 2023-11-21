package com.innim.okkycopy.domain.board.dto.request.write;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TagRequest {
    @Size(min = 1, max = 10)
    private String name;
}