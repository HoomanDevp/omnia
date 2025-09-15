package com.omnia.client.exception;

import com.omnia.core.resilience.exception.OmniaException;
import retrofit2.Response;

public interface IGatewayExceptionHandler {

    OmniaException handleError(Response<?> response);
}