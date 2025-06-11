package com.omnia.log.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestLogAttribute {

    IP("ip"),
    URI("uri"),
    METHOD("method"),
    HEADERS("headers"),
    BODY("body");

    private final String key;
}