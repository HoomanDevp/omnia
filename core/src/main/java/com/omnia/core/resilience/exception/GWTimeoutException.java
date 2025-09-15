package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class GWTimeoutException extends OmniaException {

    public GWTimeoutException() {

        this(null, (Throwable) null);
    }

    public GWTimeoutException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWTimeoutException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10006, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10006;
    }
}