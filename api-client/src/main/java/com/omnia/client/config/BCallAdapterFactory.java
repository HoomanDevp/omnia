package com.omnia.client.config;

import com.omnia.client.exception.IGatewayExceptionHandler;
import lombok.RequiredArgsConstructor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@RequiredArgsConstructor
class BCallAdapterFactory extends CallAdapter.Factory {

    private final IGatewayExceptionHandler exceptionHandler;

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        if (getRawType(returnType) != BCall.class)
            return null;

        Type bodyType = getParameterUpperBound(0, (ParameterizedType) returnType);
        return new BCallAdapter<>(bodyType, exceptionHandler);
    }
}