package ir.stts.bajet.core.resilience.exception;

import ir.stts.bajet.core.resilience.constant.IErrorCode;

public class UnauthorizedException extends BajetException {

    public UnauthorizedException() {

        this(null, (Throwable) null);
    }

    public UnauthorizedException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public UnauthorizedException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10001, errorDetails, innerException, args);
    }
}