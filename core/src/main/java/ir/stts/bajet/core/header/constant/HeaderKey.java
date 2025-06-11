package ir.stts.bajet.core.header.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HeaderKey {

    MSG_ID("msg-id", MessageDirection.BOTH),
    JOB_ID("job-id", MessageDirection.BOTH),

    USER_INFO("user-info", MessageDirection.REQUEST),
    CLIENT_INFO("client-info", MessageDirection.REQUEST),
    ACCEPT_LANGUAGE("accept-language", MessageDirection.REQUEST),
    REQUEST_SIGNATURE("request-signature", MessageDirection.REQUEST),
    REQUEST_TIMEOUT("request-timeout", MessageDirection.REQUEST),
    RETRIEVE_ATTACHMENT_HEADER("attachment; filename=", MessageDirection.RESPONSE);

    private final String key;
    private final MessageDirection direction;
}