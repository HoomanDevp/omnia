package ir.stts.bajet.core.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ir.stts.bajet.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private final DateManager dateManager;
    private final PersianDateSerializerProperties properties;

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        String persianDateString = dateManager.localDateTime2PersianDateString(value, properties.getPersianDateTimePattern());
        gen.writeString(persianDateString);
    }
}