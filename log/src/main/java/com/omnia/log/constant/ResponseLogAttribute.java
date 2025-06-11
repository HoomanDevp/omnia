package com.omnia.log.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseLogAttribute {

    STATUS("status"),
    DURATION("duration"),
    HEADERS("headers"),
    BODY("body");

    private final String key;
}