package com.omnia.storage.exception;

import com.omnia.core.resilience.exception.BajetException;
import com.omnia.core.resilience.model.ErrorSpec;

public class FmsException extends BajetException {

    public FmsException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public FmsException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public FmsException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}
