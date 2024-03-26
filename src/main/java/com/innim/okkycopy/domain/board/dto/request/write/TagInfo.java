package com.innim.okkycopy.domain.board.dto.request.write;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagInfo {

    @Size(min = 1, max = 10)
    private String name;
}