package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;

public class GWNotFoundException extends BajetException {

    public GWNotFoundException() {

        this(null, (Throwable) null);
    }

    public GWNotFoundException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWNotFoundException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10007, errorDetails, innerException, args);
    }
}