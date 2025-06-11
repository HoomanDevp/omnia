package ir.stts.bajet.client.config;

import ir.stts.bajet.client.exception.IGatewayExceptionHandler;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.CallAdapter;

import java.lang.reflect.Type;

@RequiredArgsConstructor
class BCallAdapter<T> implements CallAdapter<T, BCall<T>> {

    private final Type responseType;
    private final IGatewayExceptionHandler exceptionHandler;

    @Override
    public Type responseType() {

        return responseType;
    }

    @Override
    public BCall<T> adapt(Call<T> call) {
        return new BCall<>(call, exceptionHandler);
    }
}