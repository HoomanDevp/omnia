package com.omnia.core.resilience.exception;

import com.omnia.core.resilience.constant.IErrorCode;

public class MapstructException extends BajetException {

    public MapstructException() {

        this(null);
    }

    public MapstructException(String message) {

        super(IErrorCode._INFR_10003, message, (Throwable) null);
    }
}