package ir.stts.bajet.core.dto;

import ir.stts.bajet.core.annotation.EncryptedObject;

@EncryptedObject
public class EncryptedBajetResponseDto<T> extends BajetResponseDto<T> {

    public EncryptedBajetResponseDto(T data, String message) {
        super(data, message);
    }
}
