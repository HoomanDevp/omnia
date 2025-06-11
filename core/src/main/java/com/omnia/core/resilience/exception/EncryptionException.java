package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.model.ErrorSpec;

public class EncryptionException extends BajetException {

    public EncryptionException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public EncryptionException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public EncryptionException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}