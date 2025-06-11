package ir.stts.bajet.core.message.dto;

import ir.stts.bajet.core.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MessageResponseDto extends BaseDto {

    private Long id;
    private Integer version;

    private String key;
    private String value;
}