package ir.stts.bajet.core.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ir.stts.bajet.core.date.config.PersianDateSerializerProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private final DateManager dateManager;
    private final PersianDateSerializerProperties properties;

    @Override
    @SneakyThrows
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        String persianDateString = jsonParser.getText();
        return dateManager.persianDateString2LocalDate(persianDateString, properties.getPersianDatePattern());
    }
}