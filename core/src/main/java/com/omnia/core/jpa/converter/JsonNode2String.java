package com.omnia.core.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

@Converter(autoApply = true)
public class JsonNode2String implements BaseConverter, AttributeConverter<JsonNode, String> {

    @Override
    public String convertToDatabaseColumn(JsonNode json) {

        if (json == null)
            return null;

        return json.toString();
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {

        if (!StringUtils.hasText(dbData))
            return null;

        JsonNode jsonNode;
        try {
            jsonNode = MAPPER.readTree(dbData);
        } catch (JsonProcessingException e) {
            jsonNode = null;
        }

        return jsonNode;
    }
}