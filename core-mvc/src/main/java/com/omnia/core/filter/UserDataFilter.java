package com.omnia.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.header.constant.HeaderKey;
import com.omnia.core.header.model.ClientInfo;
import com.omnia.core.header.model.UserInfo;
import com.omnia.core.security.LegacyUserData;
import com.omnia.core.security.UserDataHolder;
import jakarta.annotation.Priority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Priority(2)
@RequiredArgsConstructor
public class UserDataFilter extends CustomOncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String jobId = MDC.get(HeaderKey.JOB_ID.getKey());
        String msgId = MDC.get(HeaderKey.MSG_ID.getKey());
        UserInfo userInfo = objectMapper.readValue(MDC.get(HeaderKey.USER_INFO.getKey()), UserInfo.class);
        ClientInfo clientInfo = objectMapper.readValue(MDC.get(HeaderKey.CLIENT_INFO.getKey()), ClientInfo.class);
        LegacyUserData legacyUserData = new LegacyUserData()
                .setPhoneNumber(request.getHeader("username"))
                .setTraceNumber(request.getHeader("tracenumber"))
                .setReferenceNumber(request.getHeader("referencenumber"))
                .setOrganId(request.getHeader("organid"))
                .setRoles(request.getHeader("roles"))
                .setChannel(request.getHeader("channel"));

        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        legacyUserData
                .setHeaders(headers)
                .setJobId(jobId)
                .setMsgId(msgId)
                .setUserInfo(userInfo)
                .setClientInfo(clientInfo);

        UserDataHolder.set(legacyUserData);

        try {
            chain.doFilter(request, response);
        } finally {
            UserDataHolder.clear();
        }
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}