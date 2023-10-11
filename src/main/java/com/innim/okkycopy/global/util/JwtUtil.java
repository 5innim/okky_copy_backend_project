package com.innim.okkycopy.global.util;


import com.innim.okkycopy.global.util.property.JwtProperty;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    public static String generateAccessToken(String userId) {
        Date date = new Date();
        date.setTime(date.getTime() + JwtProperty.accessValidTime);
        return generateToken(userId, date);
    }

    public static String generateRefreshToken(String userId) {
        Date date = new Date();
        date.setTime(date.getTime() + JwtProperty.refreshValidTime);
        return generateToken(userId, date);
    }

    private static String generateToken(String userId, Date expiration) {

        return Jwts.builder()
            .setHeader(Map.of("alg", JwtProperty.algorithm, "typ", "jwt"))
            .setClaims(Map.of("uid", userId))
            .setExpiration(expiration)
            .signWith(JwtProperty.secretKey, JwtProperty.signatureAlgorithm)
            .compact();
    }


    
}
