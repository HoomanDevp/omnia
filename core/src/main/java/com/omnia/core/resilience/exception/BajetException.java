package com.omnia.core.resilience.exception;

import io.micrometer.core.instrument.Tag;
import com.omnia.core.constant.BajetConstants;
import com.omnia.core.resilience.constant.ErrorSeverity;
import com.omnia.core.resilience.entity.Error;
import com.omnia.core.resilience.model.ErrorSpec;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Accessors(chain = true)
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, value = HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class BajetException extends RuntimeException {

    private final Object[] args;
    private final Object errorDetails;
    private final Throwable innerException;

    private int threshold;
    private int timeBoxInMinutes;
    private ErrorSeverity severity;

    @Setter
    private Boolean internal;
    @Setter
    private Boolean retryable;
    @Setter
    private Boolean userMistake;
    @Setter
    private HttpStatus httpStatus;

    private String errorCode;
    private String errorMessage;
    private String techErrorMessage;

    private boolean isErrorLoaded = false;

    public int getThreshold() {

        loadError();
        return this.threshold;
    }

    public int getTimeBoxInMinutes() {

        loadError();
        return this.timeBoxInMinutes;
    }

    public String getErrorMessage() {

        loadError();
        return this.errorMessage;
    }

    public ErrorSeverity getSeverity() {

        loadError();
        return this.severity;
    }

    public BajetException(@NotNull ErrorSpec errorCode, String... args) {

        this(errorCode, null, null, args);
    }

    public BajetException(@NotNull ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(innerException);
        this.args = args;
        this.errorCode = error.getCode();
        this.httpStatus = error.getStatus();
        this.internal = error.isInternal();
        this.retryable = error.getRetryable();
        this.userMistake = error.isUserMistake();
        this.errorDetails = errorDetails;
        this.innerException = innerException;
    }

    private void loadError() {

        if (isErrorLoaded)
            return;

        Error error;
        if (!StringUtils.hasText(this.errorCode))
            error = new Error(null, null, super.getMessage(), null, ErrorSeverity.HIGHEST, 1, false, 1);
        else
            error = Objects.requireNonNull(BajetConstants.ERRORS)
                    .stream()
                    .filter(item -> this.errorCode.equals(item.getErrorCode()))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {

                                if (list.size() != 1)
                                    return new Error(null, this.errorCode, super.getMessage(), null, ErrorSeverity.HIGHEST, 1, false, 1);

                                return list.getFirst();
                            }
                    ));

        this.errorCode = error.getErrorCode();
        if (!ObjectUtils.isEmpty(error.getErrorMessage()) && !ObjectUtils.isEmpty(this.args))
            this.errorMessage = String.format(error.getErrorMessage(), this.args);
        else
            this.errorMessage = error.getErrorMessage();

        if (!ObjectUtils.isEmpty(error.getErrorMessage()) && !ObjectUtils.isEmpty(this.args))
            this.techErrorMessage = String.format(error.getTechErrorMessage(), this.args);
        else
            this.techErrorMessage = error.getTechErrorMessage();
        this.severity = error.getSeverity();
        this.threshold = error.getThreshold();
        this.timeBoxInMinutes = error.getTimeBoxInMinutes();
        if (this.retryable == null)
            this.retryable = error.isRetryable();
        if (this.httpStatus == null)
            this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        this.isErrorLoaded = true;
    }

    @Override
    public String getMessage() {

        loadError();
        String originalMessage = ObjectUtils.isEmpty(super.getMessage()) ? "" : super.getMessage();
        return ObjectUtils.isEmpty(this.errorMessage)
                ? originalMessage
                : originalMessage + System.lineSeparator() + this.errorMessage;
    }

    @Override
    public String toString() {

        return "BajetException{" +
                "args=" + Arrays.toString(args) +
                ", errorDetails=" + errorDetails +
                ", innerException=" + innerException +
                ", threshold=" + threshold +
                ", timeBoxInMinutes=" + timeBoxInMinutes +
                ", severity=" + severity +
                ", internal=" + internal +
                ", retryable=" + retryable +
                ", userMistake=" + userMistake +
                ", httpStatus=" + httpStatus +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", techErrorMessage='" + techErrorMessage + '\'' +
                ", isErrorLoaded=" + isErrorLoaded +
                '}';
    }

    public List<Tag> tags() {

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("code", this.getErrorCode()));
        tags.add(Tag.of("internal", String.valueOf(this.getInternal())));
        tags.add(Tag.of("retryable", String.valueOf(this.getRetryable())));
        tags.add(Tag.of("status", String.valueOf(this.getHttpStatus())));
        tags.add(Tag.of("severity", String.valueOf(this.getSeverity())));
        tags.add(Tag.of("user_mistake", String.valueOf(this.getUserMistake())));
        tags.add(Tag.of("threshold", String.valueOf(this.getThreshold())));

        return tags;
    }

}