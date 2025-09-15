package com.omnia.core.otp;

import com.omnia.core.header.constant.ClientType;
import com.omnia.core.header.constant.GatewayType;

public interface TOTPManager {
    String generate(String salt, GatewayType gatewayType, ClientType clientType, long digits);

    boolean verify(String salt, GatewayType gatewayType, ClientType clientType, String code, long digits, long validDurationSeconds);
}