package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;

public class MappingException extends BajetException {

    public MappingException() {

        this(null, (Throwable) null);
    }

    public MappingException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public MappingException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10003, errorDetails, innerException, args);
    }
}