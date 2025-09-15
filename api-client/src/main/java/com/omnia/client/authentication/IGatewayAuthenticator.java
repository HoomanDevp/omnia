package com.omnia.client.authentication;

import okhttp3.Authenticator;
import okhttp3.Interceptor;

public interface IGatewayAuthenticator extends Authenticator, Interceptor {
}
