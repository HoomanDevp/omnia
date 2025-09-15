package com.omnia.core.setting.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.omnia.core.dto.BaseDto;
import com.omnia.core.setting.constant.SettingDataType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SettingResponseDto extends BaseDto {

    private Long id;
    private Integer version;
    private SettingDataType type;
    private String validation;
    private String allowedValues;
    private String key;
    private JsonNode value;
}