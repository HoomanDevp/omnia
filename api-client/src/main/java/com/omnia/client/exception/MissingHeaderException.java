package com.omnia.client.exception;

import com.omnia.core.resilience.constant.IErrorCode;
import com.omnia.core.resilience.exception.GWException;

public class MissingHeaderException extends GWException {
    public MissingHeaderException(String header) {
        super(IErrorCode._INFR_10040, header, null);
    }
}