package com.innim.okkycopy.global.util.property;

import com.innim.okkycopy.global.auth.enums.Algorithm;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperty {

    public static String algorithm;
    public static String secret;
    public static Key secretKey;
    public static SignatureAlgorithm signatureAlgorithm;
    @Value("#{environment['jwt.signature-algorithm']}")
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Value("#{environment['jwt.secret']}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @PostConstruct
    private void init() {
        secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secret.getBytes()));
        signatureAlgorithm = Algorithm.valueOf(algorithm)
            .getSignatureAlgorithm();
    }


}
