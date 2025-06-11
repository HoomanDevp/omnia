package com.omnia.core.dto;

import com.omnia.core.annotation.EncryptedObject;

@EncryptedObject
public class EncryptedBajetResponseDto<T> extends BajetResponseDto<T> {

    public EncryptedBajetResponseDto(T data, String message) {
        super(data, message);
    }
}
