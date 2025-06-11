package com.omnia.client.exception;

import com.omnia.core.resilience.exception.BajetException;
import retrofit2.Response;

public interface IGatewayExceptionHandler {

    BajetException handleError(Response<?> response);
}