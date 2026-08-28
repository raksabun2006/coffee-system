package com.kh.coffeee.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Boolean success,
        Integer status,
        String message,
        T payload,
        OffsetDateTime timestamp
) {
    public static <T> ApiResponse<T> success(Integer status, String message, T payload) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .payload(payload)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(Integer status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}