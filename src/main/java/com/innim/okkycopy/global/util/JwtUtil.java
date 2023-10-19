package com.innim.okkycopy.global.util;


import com.innim.okkycopy.global.error.exception.TokenGenerateException;
import com.innim.okkycopy.global.util.property.JwtProperty;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {

    public static String generateAccessToken(Long userId) throws TokenGenerateException {
        Date date = new Date();
        date.setTime(date.getTime() + JwtProperty.accessValidTime);
        return generateToken(userId, date, "access");
    }

    public static String generateRefreshToken(Long userId) throws TokenGenerateException {
        Date date = new Date();
        date.setTime(date.getTime() + JwtProperty.refreshValidTime);
        return generateToken(userId, date, "refresh");
    }

    private static String generateToken(Long userId, Date expiration, String tokenSub) throws TokenGenerateException{

        String generatedToken;
        try {
            generatedToken = Jwts.builder()
                .setHeader(Map.of("alg", JwtProperty.algorithm, "typ", "JWT"))
                .setExpiration(expiration)
                .setSubject(tokenSub)
                .addClaims(Map.of("uid", userId))
                .signWith(JwtProperty.secretKey, JwtProperty.signatureAlgorithm)
                .compact();
        } catch(Exception ex) {
            log.info(ex.getMessage());
            throw new TokenGenerateException("can not generate token with userId: " + "[" + userId + "]");
        }

        return generatedToken;
    }


    
}
