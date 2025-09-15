package com.omnia.core.resilience.exception;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.resilience.constant.ErrorSeverity;
import com.omnia.core.resilience.entity.Error;
import com.omnia.core.resilience.model.ErrorSpec;
import io.micrometer.core.instrument.Tag;
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

@Getter
@Accessors(chain = true)
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, value = HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class OmniaException extends RuntimeException {

    private final transient Object[] args;
    private final transient Object errorDetails;
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

    protected OmniaException(@NotNull ErrorSpec errorCode, String... args) {

        this(errorCode, null, null, args);
    }

    protected OmniaException(@NotNull ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

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

    protected abstract ErrorSpec getDefaultErrorSpec();

    private void loadError() {

        if (isErrorLoaded)
            return;

        Error error;
        if (!StringUtils.hasText(this.errorCode))
            error = new Error(null, null, super.getMessage(), null, ErrorSeverity.HIGHEST, 1, false, 1);
        else
            error = OmniaConstants.ERRORS.getOrDefault(this.errorCode, OmniaConstants.ERRORS.get(getDefaultErrorSpec().getCode()));

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
}