package com.omnia.client.config;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.omnia.client.exception.IGatewayExceptionHandler;
import com.omnia.core.resilience.exception.GWNotAvailableException;
import com.omnia.core.resilience.exception.GWTimeoutException;
import com.omnia.core.resilience.exception.MappingException;
import com.omnia.core.resilience.exception.OmniaException;
import com.omnia.core.security.LegacyUserData;
import com.omnia.core.security.UserDataHolder;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class BCall<T> {
    private static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private final Call<T> call;
    private final IGatewayExceptionHandler exceptionHandler;

    public T execute() {
        try {
            return executeAsync().get(); // wait for result
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            }
            throw new IllegalStateException(e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Virtual thread execution interrupted", e);
        }
    }

    public CompletableFuture<T> executeAsync() {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        final LegacyUserData userData = UserDataHolder.get();
        return CompletableFuture.supplyAsync(() -> execute(call, contextMap, userData, exceptionHandler), VIRTUAL_EXECUTOR);
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

    private T execute(Call<T> call, Map<String, String> contextMap, LegacyUserData headerSpec, IGatewayExceptionHandler exceptionHandler) {
        try {
            MDC.setContextMap(contextMap);
            UserDataHolder.set(headerSpec);
            Response<T> response = call.execute();
            if (response.isSuccessful()) return response.body();
            throw exceptionHandler.handleError(response);
        } catch (OmniaException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException)
                throw new GWTimeoutException(e);
            if (e instanceof MismatchedInputException || e instanceof InvalidDefinitionException)
                throw new GWNotAvailableException(new MappingException(e));
            throw new GWNotAvailableException(e);
        } finally {
            MDC.clear();
            UserDataHolder.clear();
        }
    }
}