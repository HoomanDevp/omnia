package com.omnia.core.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.omnia.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class DateSerializer extends JsonSerializer<Date> {

    private final DateManager dateManager;
    private final PersianDateSerializerProperties properties;

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        String persianDateString = dateManager.date2PersianDateString(value, properties.getPersianDatePattern());
        gen.writeString(persianDateString);
    }
}