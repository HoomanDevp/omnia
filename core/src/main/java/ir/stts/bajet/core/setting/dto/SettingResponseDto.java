package ir.stts.bajet.core.setting.dto;

import com.fasterxml.jackson.databind.JsonNode;
import ir.stts.bajet.core.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SettingResponseDto extends BaseDto {

    private Long id;
    private Integer version;

    private String key;
    private JsonNode value;
}