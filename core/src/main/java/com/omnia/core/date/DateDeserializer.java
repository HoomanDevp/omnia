package com.omnia.core.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.omnia.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DateDeserializer extends JsonDeserializer<Date> {

    private final DateManager dateManager;
    private final PersianDateSerializerProperties properties;

    @Override
    @SneakyThrows
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        String persianDateString = jsonParser.getText();
        return dateManager.persianDateString2Date(persianDateString, properties.getPersianDatePattern());
    }
}