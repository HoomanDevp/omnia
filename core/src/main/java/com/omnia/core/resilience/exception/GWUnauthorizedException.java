package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class GWUnauthorizedException extends OmniaException {

    public GWUnauthorizedException() {

        this(null, (Throwable) null);
    }

    public GWUnauthorizedException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWUnauthorizedException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10005, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10005;
    }
}