package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.model.ErrorSpec;

public class MapstructException extends OmniaException {

    public MapstructException() {

        this(null);
    }

    public MapstructException(String message) {

        super(IErrorCode._INFR_10003, message, (Throwable) null);
    }

    @Override
    protected ErrorSpec getDefaultErrorSpec() {
        return IErrorCode._INFR_10003;
    }
}