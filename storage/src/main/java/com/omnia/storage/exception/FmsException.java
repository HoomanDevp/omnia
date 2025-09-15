package com.omnia.storage.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.exception.OmniaException;
import com.omnia.core.resilience.model.ErrorSpec;

public class FmsException extends OmniaException {

    public FmsException(ErrorSpec error) {

        this(error, null, (Throwable) null);
    }

    public FmsException(ErrorSpec error, Throwable innerException, String... args) {

        this(error, null, innerException, args);
    }

    public FmsException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10033;
    }
}
