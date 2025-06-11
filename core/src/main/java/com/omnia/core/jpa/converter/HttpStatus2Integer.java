package com.omnia.core.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.http.HttpStatus;

@Converter
public class HttpStatus2Integer implements AttributeConverter<HttpStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(HttpStatus status) {

        if (status == null)
            return null;

        return status.value();
    }

    @Override
    public HttpStatus convertToEntityAttribute(Integer value) {

        return HttpStatus.resolve(value);
    }
}