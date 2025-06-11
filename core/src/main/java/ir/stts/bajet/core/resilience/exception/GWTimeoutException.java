package ir.stts.bajet.core.resilience.exception;

import ir.stts.bajet.core.resilience.constant.IErrorCode;

public class GWTimeoutException extends BajetException {

    public GWTimeoutException() {

        this(null, (Throwable) null);
    }

    public GWTimeoutException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWTimeoutException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10006, errorDetails, innerException, args);
    }
}