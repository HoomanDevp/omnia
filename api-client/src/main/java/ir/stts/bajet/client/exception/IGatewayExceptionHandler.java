package ir.stts.bajet.client.exception;

import ir.stts.bajet.core.resilience.exception.BajetException;
import retrofit2.Response;

public interface IGatewayExceptionHandler {

    BajetException handleError(Response<?> response);
}