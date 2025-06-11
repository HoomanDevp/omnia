package ir.stts.bajet.core.resilience.exception;

import ir.stts.bajet.core.resilience.constant.IErrorCode;
import ir.stts.bajet.core.resilience.model.ErrorSpec;

public class NotFoundException extends BajetException {

    private static final ErrorSpec DEFAULT_ERROR = IErrorCode._INFR_10002;

    public NotFoundException() {

        this(DEFAULT_ERROR, null, (Throwable) null);
    }

    public NotFoundException(Throwable innerException, String... args) {

        this(DEFAULT_ERROR, null, innerException, args);
    }

    public NotFoundException(Object errorDetails, Throwable innerException, String... args) {

        this(DEFAULT_ERROR, errorDetails, innerException, args);
    }

    public NotFoundException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public NotFoundException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public NotFoundException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}