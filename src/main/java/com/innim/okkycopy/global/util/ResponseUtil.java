package com.innim.okkycopy.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.ErrorResponse;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String jsonFrom(T obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T objectOf(ServletInputStream inputStream, Class<T> type)
        throws IOException {
        return objectMapper.readValue(inputStream, type);
    }

    public static void setContentTypeToJson(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    public static <T> void setResponseToObject(HttpServletResponse response, T obj)
        throws IOException {
        setContentTypeToJson(response);
        String body = jsonFrom(obj);
        response.getWriter().write(body);
    }

    public static void setResponseToErrorResponse(HttpServletResponse response, ErrorCase errorCase)
        throws IOException {
        setContentTypeToJson(response);
        response.setStatus(errorCase.getStatus().value());
        String body = jsonFrom(new ErrorResponse(errorCase.getCode(), errorCase.getMessage()));
        response.getWriter().write(body);
    }

    public static void addCookie(HttpServletResponse response, String name, String value) {
        String cookieHeader = String.format("%s=%s; HttpOnly; Secure; Path=/; SameSite=Strict", name, value);
        response.addHeader("Set-Cookie", cookieHeader);
    }
}
