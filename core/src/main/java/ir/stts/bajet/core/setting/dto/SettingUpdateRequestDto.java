package ir.stts.bajet.core.setting.dto;

import com.fasterxml.jackson.databind.JsonNode;
import ir.stts.bajet.core.dto.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingUpdateRequestDto extends BaseDto {

    @NotNull
    private Long id;

    @NotNull
    private Integer version;

    @NotBlank
    private String key;

    @NotBlank
    private JsonNode value;
}