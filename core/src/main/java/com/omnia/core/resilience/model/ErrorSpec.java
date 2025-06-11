package com.omnia.core.resilience.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ErrorSpec {

    private String code;
    private HttpStatus status;
    private boolean internal;
    private Boolean retryable;
    private boolean userMistake;
    private String developerDescription;

    @Override
    public String toString() {
        return getCode();
    }
}