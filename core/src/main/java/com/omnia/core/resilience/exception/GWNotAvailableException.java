package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;

public class GWNotAvailableException extends BajetException {

    public GWNotAvailableException() {

        this(null, (Throwable) null);
    }

    public GWNotAvailableException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWNotAvailableException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10008, errorDetails, innerException, args);
    }
}