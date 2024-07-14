package com.innim.okkycopy.global.util;

import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.util.property.JwtProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;


public class JwtUtil {

    public static String generateAccessToken(Long userId, Date loginDate) {
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.accessValidTime);
        return generateToken(userId, expiredDate, loginDate, "access");
    }

    public static String generateRefreshToken(Long userId, Date loginDate) {
        Date expiredDate = new Date(loginDate.getTime() + JwtProperty.refreshValidTime);
        return generateToken(userId, expiredDate, loginDate, "refresh");
    }

    public static String generateToken(Long userId, Date expiredDate, Date loginDate, String tokenSub) {

        String generatedToken;
        try {
            generatedToken = Jwts.builder()
                .setHeader(Map.of("alg", JwtProperty.algorithm, "typ", "JWT"))
                .setExpiration(expiredDate)
                .setSubject(tokenSub)
                .addClaims(Map.of("uid", userId))
                .addClaims(Map.of("lat", loginDate))
                .signWith(JwtProperty.secretKey, JwtProperty.signatureAlgorithm)
                .compact();
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_JWT_GENERATE_FAIL);
        }

        return generatedToken;
    }


    public static Claims validateToken(String token) {

        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(JwtProperty.secretKey).build();
            return parser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            throw new StatusCode401Exception(ErrorCase._401_TOKEN_EXPIRED);
        } catch (Exception ex) {
            throw new StatusCode401Exception(ErrorCase._401_TOKEN_VALIDATE_FAIL);
        }

    }

    public static void checkTokenIsLogoutOrIsGeneratedBeforeLogin(Member member, Date date) {
        Date lastLoginDate = (Date) Timestamp.valueOf(member.getLoginDate());
        Date lastLogoutDate =
            (member.getLogoutDate() == null) ? null : (Date) Timestamp.valueOf(member.getLogoutDate());
        if (date.compareTo(lastLoginDate) < 0) {
            throw new StatusCode401Exception(ErrorCase._401_BEFORE_THEN_LAST_LOGIN_DATE);
        } else if (lastLogoutDate != null && lastLoginDate.compareTo(lastLogoutDate) < 0) {
            throw new StatusCode401Exception(ErrorCase._401_IS_LOGOUT_MEMBER);
        }
    }


    //  it was used, when this service use header to transfer jwt,
    //  but now, using cookie. so those methods below this line is no usage.

    /*
    public static boolean prefixNotMatched(String value) {
        return !value.startsWith(JwtProperty.prefix);
    }

    public static String extractTokenWithoutPrefix(String value) {
        return value.substring(JwtProperty.prefix.length());
    }
     */

}
