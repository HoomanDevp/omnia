package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class MappingException extends OmniaException {

    public MappingException() {

        this(null, (Throwable) null);
    }

    public MappingException(Throwable innerException, String... args) {

        this(null, innerException, args);
    }

    public MappingException(Object errorDetails, Throwable innerException, String... args) {

        super(IErrorCode._INFR_10003, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10003;
    }
}