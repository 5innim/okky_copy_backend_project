package com.innim.okkycopy.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.ErrorResponse;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
public class RequestResponseUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String objectToJson(T obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T jsonToObject(ServletInputStream inputStream, Class<T> type)
        throws IOException {
        return objectMapper.readValue(inputStream, type);
    }

    public static void makeJsonResponseHeader(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    public static void makeExceptionResponseForFilter(HttpServletResponse response, ErrorCode errorCode)
        throws IOException {
        makeJsonResponseHeader(response);
        response.setStatus(errorCode.getStatus().value());

        String body = objectToJson(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
        response.getWriter().write(body);
    }

}
