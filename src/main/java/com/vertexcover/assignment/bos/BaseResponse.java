package com.vertexcover.assignment.bos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BaseResponse<T> {
    private T data;
    private String message;

    public static <U> BaseResponse<U> success(U data, String message) {
        return BaseResponse.<U>builder()
                .data(data)
                .message(message)
                .build();
    }

    public static <U> BaseResponse<U> failure(U data, String message) {
        return BaseResponse.<U>builder()
                .data(data)
                .message(message)
                .build();
    }

}
