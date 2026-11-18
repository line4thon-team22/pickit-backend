package com.pickit.backend.common.dto;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "OK", data);
    }

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }
}
