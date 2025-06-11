package ir.stts.bajet.core.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ir.stts.bajet.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    private final DateManager dateManager;
    private final PersianDateSerializerProperties properties;

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        String persianDateString = dateManager.localDate2PersianDateString(value, properties.getPersianDatePattern());
        gen.writeString(persianDateString);
    }
}