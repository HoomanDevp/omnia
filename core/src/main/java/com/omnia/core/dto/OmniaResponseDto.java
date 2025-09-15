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
public class OmniaResponseDto<T> {

    private final T data;
    private final String message;

    @AssertTrue(message = "Either data or error must be provided, but not both.")
    private boolean isValid() {

        return data != null;
    }

    public static <T> OmniaResponseDto<T> success(T data) {

        return new OmniaResponseDto<>(data, null);
    }

    public static <T> OmniaResponseDto<T> success(T data, String message) {

        return new OmniaResponseDto<>(data, message);
    }
}