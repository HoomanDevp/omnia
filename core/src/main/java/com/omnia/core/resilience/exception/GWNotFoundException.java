package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class GWNotFoundException extends OmniaException {

    public GWNotFoundException() {

        this(null, (Throwable) null);
    }

    public GWNotFoundException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public GWNotFoundException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10007, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10007;
    }
}