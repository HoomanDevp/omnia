package com.omnia.core.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
@RequiredArgsConstructor
public class StringSet2StringWithSharpSeparate implements AttributeConverter<Set<String>, String> {

    private static final String DELIMITER = "#";

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {

        if (attribute != null)
            return String.join(DELIMITER, attribute);

        return null;
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {

        if (StringUtils.hasText(dbData)) {

            String[] split = dbData.split(DELIMITER);
            return Arrays.stream(split).collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }
}