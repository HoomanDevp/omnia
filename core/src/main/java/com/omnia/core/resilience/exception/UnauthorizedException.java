package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class UnauthorizedException extends OmniaException {

    public UnauthorizedException() {

        this(null, (Throwable) null);
    }

    public UnauthorizedException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public UnauthorizedException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10001, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10001;
    }
}