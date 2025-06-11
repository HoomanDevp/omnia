package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class HeaderInfoException extends BajetException {

    public HeaderInfoException() {

        this(null, null, (Throwable) null);
    }

    public HeaderInfoException(Throwable innerException, String... args) {

        this(null, null, innerException, args);
    }

    public HeaderInfoException(Object errorDetails, Throwable innerException, String... args) {

        this(IErrorCode._INFR_10004, errorDetails, innerException, args);
    }

    public HeaderInfoException(ErrorSpec error, Object errorDetails, Throwable innerException, String... args) {

        super(error, errorDetails, innerException, args);
    }
}