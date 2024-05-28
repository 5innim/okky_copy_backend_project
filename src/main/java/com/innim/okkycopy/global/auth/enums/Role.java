package com.innim.okkycopy.global.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    MAIL_INVALID_USER("ROLE_MAIL_INVALID_USER"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;
}
