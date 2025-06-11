package com.omnia.core.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.omnia.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateManager dateManager;
    private final PersianDateSerializerProperties properties;

    @Override
    @SneakyThrows
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        String persianDateString = jsonParser.getText();
        return dateManager.persianDateString2LocalDateTime(persianDateString, properties.getPersianDateTimePattern());
    }
}