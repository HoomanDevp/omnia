package com.omnia.core.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Converter
@RequiredArgsConstructor
public class StringList2StringWithSharpSeparate implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = "#";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {

        if (attribute != null)
            return String.join(DELIMITER, attribute);

        return null;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {

        if (StringUtils.hasText(dbData)) {

            String[] split = dbData.split(DELIMITER);
            return Arrays.stream(split).toList();
        }

        return null;
    }
}