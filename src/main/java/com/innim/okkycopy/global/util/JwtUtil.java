package com.innim.okkycopy.global.util;


import com.innim.okkycopy.global.util.property.JwtProperty;
import io.jsonwebtoken.Jwts;
import java.util.Map;

public class JwtUtil {

    public static String generateToken(String userId) {

        return Jwts.builder()
            .setHeader(Map.of("alg", JwtProperty.algorithm, "typ", "jwt"))
            .setClaims(Map.of("uid", userId))
            .signWith(JwtProperty.secretKey, JwtProperty.signatureAlgorithm)
            .compact();
    }


    
}
