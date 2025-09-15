package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class FileIllegalTypeException extends OmniaException {

    private static final ErrorSpec DEFAULT_ERROR = IErrorCode._INFR_10009;

    public FileIllegalTypeException() {

        this(DEFAULT_ERROR, null, (Throwable) null);
    }

    public FileIllegalTypeException(Throwable innerException, String... args) {

        this(DEFAULT_ERROR, null, innerException, args);
    }

    public FileIllegalTypeException(Object errorDetails, Throwable innerException, String... args) {

        this(DEFAULT_ERROR, errorDetails, innerException, args);
    }

    public FileIllegalTypeException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public FileIllegalTypeException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public FileIllegalTypeException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return DEFAULT_ERROR;
    }
}