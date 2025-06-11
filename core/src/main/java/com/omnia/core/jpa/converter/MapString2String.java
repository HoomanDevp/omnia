package com.omnia.core.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Converter(autoApply = true)
public class MapString2String implements BaseConverter, AttributeConverter<Map<String, String>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {

        if (attribute != null)
            try {
                return MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                return null;
            }

        return null;
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {

        if (!ObjectUtils.isEmpty(dbData))
            try {
                //noinspection unchecked
                return MAPPER.readValue(dbData, Map.class);
            } catch (JsonProcessingException e) {
                return null;
            }

        return null;
    }
}