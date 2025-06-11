package ir.stts.bajet.client.config;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import ir.stts.bajet.client.exception.IGatewayExceptionHandler;
import ir.stts.bajet.core.resilience.exception.GWNotAvailableException;
import ir.stts.bajet.core.resilience.exception.GWTimeoutException;
import ir.stts.bajet.core.resilience.exception.MappingException;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.SocketTimeoutException;

@RequiredArgsConstructor
public class BCall<T> {

    private final Call<T> call;
    private final IGatewayExceptionHandler exceptionHandler;

    public T execute() {

        Response<T> response;
        try {
            response = call.execute();
        } catch (Exception e) {

            if (e instanceof SocketTimeoutException)
                throw new GWTimeoutException(e);
            if (e instanceof MismatchedInputException || e instanceof InvalidDefinitionException) {
                MappingException mappingException = new MappingException(e);
                throw new GWNotAvailableException(mappingException);
            }

            throw new GWNotAvailableException(e);
        }

        if (response.isSuccessful())
            return response.body();

        throw exceptionHandler.handleError(response);
    }

    public void enqueue(Callback<T> callback) {

        Response<T> response;
        try {
            response = call.execute();
        } catch (Exception e) {

            if (e instanceof SocketTimeoutException) {

                callback.onFailure(call, new GWTimeoutException(e));
                return;
            }
            if (e instanceof MismatchedInputException || e instanceof InvalidDefinitionException) {

                MappingException mappingException = new MappingException(e);
                callback.onFailure(call, new GWNotAvailableException(mappingException));
                return;
            }

            callback.onFailure(call, new GWNotAvailableException(e));
            return;
        }

        if (response.isSuccessful())
            callback.onResponse(call, response);

        callback.onFailure(call, exceptionHandler.handleError(response));
    }
}