package com.omnia.core.resilience.dto;

import com.omnia.core.dto.BaseDto;
import com.omnia.core.resilience.constant.ErrorSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorUpdateRequestDto extends BaseDto {

    @NotNull
    private Long id;

    @NotNull
    private Integer version;

    @NotBlank
    private String errorCode;

    @NotBlank
    private String errorMessage;

    @NotBlank
    private ErrorSeverity severity;

    @PositiveOrZero
    private int threshold;

    @PositiveOrZero
    private int timeBoxInMinutes;
}