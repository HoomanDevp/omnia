package com.omnia.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.dto.OmniaErrorResponseDto;
import com.omnia.core.header.constant.HeaderKey;
import com.omnia.core.util.RequestSignatureUtils;
import com.omnia.log.AppLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@ConditionalOnProperty(
        prefix = OmniaConstants.OMNIA_BASE_PACKAGE + ".core.request-signature-verification",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
@Component
@RequiredArgsConstructor
public class RequestSignatureVerificationFilter extends CustomOncePerRequestFilter {
    private final AppLogger logger = new AppLogger(RequestSignatureVerificationFilter.class);
    private final ObjectMapper objectMapper;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String signatureHeader = request.getHeader(HeaderKey.REQUEST_SIGNATURE.getKey());
        String timeoutHeader = request.getHeader(HeaderKey.REQUEST_TIMEOUT.getKey());
        String clientInfo = request.getHeader(HeaderKey.CLIENT_INFO.getKey());

        if (!StringUtils.hasText(signatureHeader)) {
            super.createResponseError(response, new OmniaErrorResponseDto("", "BAD DATA", false));
            return;
        }

        Long timeout = null;
        if (StringUtils.hasText(timeoutHeader)) {
            try {
                timeout = Long.parseLong(timeoutHeader);
                if (System.currentTimeMillis() > timeout) {
                    super.createResponseError(response, new OmniaErrorResponseDto("", "BAD DATA", false));
                    return;
                }
            } catch (NumberFormatException e) {
                super.createResponseError(response, new OmniaErrorResponseDto("", "BAD DATA", false));
                return;
            }
        }

        byte[] reqBody = request.getInputStream().readAllBytes();
        String method = request.getMethod().toLowerCase();
        String path = request.getRequestURI();
        String body = new String(reqBody);
        if (!StringUtils.hasText(body)) {
            body = OmniaConstants.EMPTY_BODY;
        }

        String expectedSignature;
        if (timeout != null) {
            expectedSignature = RequestSignatureUtils.computeSignature(method, path, timeout * 2, clientInfo, body);
        } else {
            expectedSignature = RequestSignatureUtils.computeSignature(method, path, clientInfo, body);
        }

        if (!expectedSignature.equals(signatureHeader)) {
            logger.warnF("Invalid Request signature: expected={}, actual={}", expectedSignature, signatureHeader);
            super.createResponseError(response, new OmniaErrorResponseDto("", "BAD DATA", false));
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request) {
            @Override
            public ServletInputStream getInputStream() {
                return new BodyInputStream(reqBody);
            }
        };

        filterChain.doFilter(wrappedRequest, response);
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    private static class BodyInputStream extends ServletInputStream {

        private final InputStream delegate;

        public BodyInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
