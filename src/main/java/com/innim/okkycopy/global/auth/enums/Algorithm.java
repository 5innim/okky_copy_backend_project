package com.innim.okkycopy.global.auth.enums;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Algorithm {
    HS256(SignatureAlgorithm.HS256);

    private final SignatureAlgorithm signatureAlgorithm;

}