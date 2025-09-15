package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class InvalidDataException extends OmniaException {

    public InvalidDataException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public InvalidDataException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public InvalidDataException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10036;
    }
}