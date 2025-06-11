package ir.stts.bajet.core.resilience.dto;

import ir.stts.bajet.core.dto.BaseDto;
import ir.stts.bajet.core.resilience.constant.ErrorSeverity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ErrorResponseDto extends BaseDto {

    private Long id;
    private Integer version;

    private String errorCode;
    private String errorMessage;
    private ErrorSeverity severity;
    private int threshold;
    private int timeBoxInMinutes;
}