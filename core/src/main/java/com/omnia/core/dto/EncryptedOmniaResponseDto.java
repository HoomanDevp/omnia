package com.omnia.core.dto;

import com.omnia.core.annotation.EncryptedObject;

@EncryptedObject
public class EncryptedOmniaResponseDto<T> extends OmniaResponseDto<T> {

    public EncryptedOmniaResponseDto(T data, String message) {
        super(data, message);
    }


    public static <T> EncryptedOmniaResponseDto<T> success(T data) {

        return new EncryptedOmniaResponseDto<>(data, null);
    }

    public static <T> EncryptedOmniaResponseDto<T> success(T data, String message) {

        return new EncryptedOmniaResponseDto<>(data, message);
    }
}
