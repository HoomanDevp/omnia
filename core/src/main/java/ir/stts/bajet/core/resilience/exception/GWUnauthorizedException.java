package ir.stts.bajet.core.resilience.exception;

import ir.stts.bajet.core.resilience.constant.IErrorCode;

public class GWUnauthorizedException extends BajetException {

    public GWUnauthorizedException() {

        this(null, (Throwable) null);
    }

    public GWUnauthorizedException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWUnauthorizedException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10005, errorDetails, innerException, args);
    }
}