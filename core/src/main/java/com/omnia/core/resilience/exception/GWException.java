package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.model.ErrorSpec;

public class GWException extends BajetException {

    public GWException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public GWException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public GWException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}