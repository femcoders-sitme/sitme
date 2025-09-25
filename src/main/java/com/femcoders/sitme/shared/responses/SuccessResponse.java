package com.femcoders.sitme.shared.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(true, message, data);
    }

    public static SuccessResponse<Void> of(String message) {
        return new SuccessResponse<>(true, message, null);
    }
}
