package com.innim.okkycopy.domain.board.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExpressionType {
    LIKE(0),
    HATE(1);

    private final Integer value;
}
