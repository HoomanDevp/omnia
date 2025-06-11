package ir.stts.bajet.core.resilience.exception;

import ir.stts.bajet.core.resilience.constant.IErrorCode;
import ir.stts.bajet.core.resilience.model.ErrorSpec;

public class FileIllegalSizeException extends BajetException {

    private static final ErrorSpec DEFAULT_ERROR = IErrorCode._INFR_10010;

    public FileIllegalSizeException() {

        this(DEFAULT_ERROR, null, (Throwable) null);
    }

    public FileIllegalSizeException(Throwable innerException, String... args) {

        this(DEFAULT_ERROR, null, innerException, args);
    }

    public FileIllegalSizeException(Object errorDetails, Throwable innerException, String... args) {

        this(DEFAULT_ERROR, errorDetails, innerException, args);
    }

    public FileIllegalSizeException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public FileIllegalSizeException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public FileIllegalSizeException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}