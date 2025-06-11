package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.model.ErrorSpec;

public class DecryptionException extends BajetException {

    public DecryptionException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public DecryptionException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public DecryptionException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}