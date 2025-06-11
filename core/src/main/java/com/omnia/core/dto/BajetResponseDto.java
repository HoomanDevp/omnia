package com.omnia.core.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true)
public class BajetResponseDto<T> {

    private final T data;
    private final String message;

    @AssertTrue(message = "Either data or error must be provided, but not both.")
    private boolean isValid() {

        return data != null;
    }

    public static <T> BajetResponseDto<T> success(T data) {

        return new BajetResponseDto<T>(data, null);
    }

    public static <T> BajetResponseDto<T> success(T data, String message) {

        return new BajetResponseDto<T>(data, message);
    }
}