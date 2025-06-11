package com.omnia.core.message.dto;

import com.omnia.core.dto.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageUpdateRequestDto extends BaseDto {

    @NotNull
    private Long id;

    @NotNull
    private Integer version;

    @NotBlank
    private String key;

    @NotBlank
    private String value;
}