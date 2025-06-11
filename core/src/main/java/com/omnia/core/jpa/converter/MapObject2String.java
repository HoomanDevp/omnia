package com.omnia.core.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Converter(autoApply = true)
public class MapObject2String implements BaseConverter, AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {

        if (attribute != null)
            try {
                return MAPPER.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                return null;
            }

        return null;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {

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