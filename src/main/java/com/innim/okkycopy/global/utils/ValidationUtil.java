package com.innim.okkycopy.global.utils;

import com.innim.okkycopy.global.enums.ErrorCode;

public class ValidationUtil {
    public static ErrorCode retreiveErrorCode(String field) {
        switch(field) {
            case("id"):
                return ErrorCode._400_INVALID_ID;
            case("password"):
                return ErrorCode._400_INVALID_PW;
            case("email"):
                return ErrorCode._400_INVALID_EMAIL;
            case("name"):
                return ErrorCode._400_INVALID_NAME;
            case("nickname"):
                return ErrorCode._400_INVALID_NICKNAME;
            default:
                return ErrorCode._400_INVALID_UNEXPECTED;
        }
    }
}
