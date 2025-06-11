package com.omnia.core.header.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageDirection {

    REQUEST,
    RESPONSE,
    BOTH
}