package com.omnia.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
public class OmniaErrorResponseDto extends BaseDto {

    private final String errorCode;
    private final String errorMessage;
    private final boolean retryable;

    private Object errorDetails;
}