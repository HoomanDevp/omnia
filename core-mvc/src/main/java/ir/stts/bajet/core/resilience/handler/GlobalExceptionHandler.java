package ir.stts.bajet.core.resilience.handler;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.dto.BajetErrorResponseDto;
import ir.stts.bajet.core.resilience.constant.IErrorCode;
import ir.stts.bajet.core.resilience.exception.BajetException;
import ir.stts.bajet.core.resilience.handler.config.ErrorMonitoringProperties;
import ir.stts.bajet.core.resource.MessageResourceManager;
import ir.stts.bajet.log.AppLogger;
import ir.stts.bajet.log.LogSpec;
import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Priority(3)
@AllArgsConstructor
@RestControllerAdvice
@SuppressWarnings("NullableProblems")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final AppLogger appLogger = new AppLogger(GlobalExceptionHandler.class);
    private final MeterRegistry meterRegistry;
    private final ErrorMonitoring errorMonitoring;
    private final ErrorMonitoringProperties properties;
    private final MessageResourceManager messageResourceManager;

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.method-not-allowed", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.unsupported-media-type", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.media-type-not-acceptable", HttpStatus.NOT_ACCEPTABLE);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.missing-path-variable", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.missing-servlet-request-parameter", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.missing-servlet-request-part", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.servlet-request-binding", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.no-handler-found", HttpStatus.NOT_FOUND);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.no-resource-found", HttpStatus.NOT_FOUND);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.http-message-not-readable", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);
        return provideResponse("core.resilience.handler.http-message-not-writable", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> detail = new HashMap<>();
        for (FieldError fieldError : fieldErrors)
            detail.put(
                    fieldError
                            .getField()
                            .replaceAll("([a-z])([A-Z])", "$1_$2")
                            .toLowerCase(),
                    fieldError.getDefaultMessage());

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10011.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10011.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.method-argument-not-valid")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10011.getRetryable(),
                detail),
                IErrorCode._INFR_10011.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        List<ParameterValidationResult> validationResults = ex.getParameterValidationResults();
        Map<String, String> detail = validationResults.stream()
                .filter(result -> result instanceof ParameterErrors)
                .map(result -> (ParameterErrors) result)
                .flatMap(errorsResult -> errorsResult.getFieldErrors().stream())
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("No error message")
                ));

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10012.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10012.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.method-validation")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10012.getRetryable(),
                detail),
                IErrorCode._INFR_10012.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10013.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10013.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.async-request-timeout")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10013.getRetryable()),
                IErrorCode._INFR_10013.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10014.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10014.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.error-response")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10014.getRetryable()),
                IErrorCode._INFR_10014.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10015.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10015.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.max-upload-size-exceeded")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10015.getRetryable()),
                IErrorCode._INFR_10015.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleConversionNotSupported(
            ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        String propertyName = ex.getPropertyName();
        String errorMessage = messageResourceManager.getMessage(
                "core.resilience.handler.conversion-not-supported.detail",
                ex.getPropertyName(),
                ex.getRequiredType() != null
                        ? ex.getRequiredType().getName()
                        : "Unknown");

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10016.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10016.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.conversion-not-supported")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10016.getRetryable(),
                new HashMap<>() {{
                    put(propertyName, errorMessage);
                }}),
                IErrorCode._INFR_10016.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        printLog(ex);

        String propertyName = ex.getPropertyName();
        String errorMessage = messageResourceManager.getMessage(
                "core.resilience.handler.type-mismatch.detail",
                ex.getPropertyName(),
                ex.getValue() != null
                        ? ex.getValue().toString()
                        : null,
                ex.getRequiredType() != null
                        ? ex.getRequiredType().getSimpleName()
                        : "Unknown");

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10017.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10017.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.type-mismatch")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10017.getRetryable(),
                new HashMap<>() {{
                    put(propertyName, errorMessage);
                }}),
                IErrorCode._INFR_10017.getStatus());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodValidationException(
            MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        printLog(ex);

        List<ParameterValidationResult> validationResults = ex.getParameterValidationResults();
        Map<String, String> detail = validationResults.stream()
                .filter(result -> result instanceof ParameterErrors)
                .map(result -> (ParameterErrors) result)
                .flatMap(errorsResult -> errorsResult.getFieldErrors().stream())
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("No error message")
                ));

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10012.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10012.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.method-validation")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10012.getRetryable(),
                detail),
                IErrorCode._INFR_10012.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {

        printLog(ex);

        Map<String, String> detail = ex
                .getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10018.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10018.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.constraint-violation")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10018.getRetryable(),
                detail),
                IErrorCode._INFR_10018.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        printLog(ex);

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10019.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10019.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.data-integrity-violation")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10019.getRetryable()),
                IErrorCode._INFR_10019.getStatus());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException ex) {

        printLog(ex);

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10020.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10020.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.multipart")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10020.getRetryable()),
                IErrorCode._INFR_10020.getStatus());
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {

        printLog(ex);

        ir.stts.bajet.core.resilience.entity.Error error = BajetConstants
                .ERRORS
                .stream()
                .filter(e -> e.getErrorCode().equals(IErrorCode._INFR_10021.getCode()))
                .findFirst().orElse(null);

        return new ResponseEntity<>(new BajetErrorResponseDto(
                IErrorCode._INFR_10021.getCode(),
                error == null
                        ? messageResourceManager.getMessage("core.resilience.handler.object-optimistic-locking-failure")
                        : error.getErrorMessage(),
                IErrorCode._INFR_10021.getRetryable()),
                IErrorCode._INFR_10021.getStatus());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {

        printLog(exception);
        return provideResponse(
                "core.resilience.handler.error-response",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {

        printLog(exception);
        return provideResponse(
                "core.resilience.handler.error-response",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BajetException.class)
    public ResponseEntity<BajetErrorResponseDto> handleBajetException(BajetException ex) {

        printLog(ex);
        if (properties.isEnabled() && ex.getThreshold() > 0 && ex.getTimeBoxInMinutes() > 0)
            errorMonitoring.registerError(ex);

        return provideResponse(ex);
    }

    private void printLog(Throwable throwable) {
        appLogger.error(throwable.getMessage(), throwable);
    }

    private ResponseEntity<Object> provideResponse(String messageKey, HttpStatus httpStatus) {

        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("message_key", messageKey));
        tags.add(Tag.of("status", httpStatus.value() + ""));
        meterRegistry.counter(BajetConstants.UNKNOWN_ERROR_COUNTER, tags).increment();
        return new ResponseEntity<>(
                new BajetErrorResponseDto(
                        "",
                        messageResourceManager.getMessage(messageKey), true),
                httpStatus);
    }

    private void printLog(BajetException exception) {

        appLogger.error(LogSpec.ofException(exception.toString(), exception).toString());
    }

    private ResponseEntity<BajetErrorResponseDto> provideResponse(BajetException exception) {

        meterRegistry.counter(BajetConstants.ERROR_COUNTER, exception.tags()).increment();
        return new ResponseEntity<>(
                new BajetErrorResponseDto(
                        exception.getErrorCode(),
                        exception.getErrorMessage(),
                        exception.getRetryable(),
                        exception.getInternal() ? exception.getErrorDetails() : null),
                exception.getHttpStatus());
    }
}