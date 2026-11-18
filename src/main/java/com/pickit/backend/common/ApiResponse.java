package com.pickit.backend.common;

/** 모든 API가 함께 쓰는 응답 형식. */
public record ApiResponse<T>(int status, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "OK", data);
    }
}
