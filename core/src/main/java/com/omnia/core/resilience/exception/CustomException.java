package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class CustomException extends OmniaException {

    public CustomException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public CustomException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public CustomException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10034;
    }
}