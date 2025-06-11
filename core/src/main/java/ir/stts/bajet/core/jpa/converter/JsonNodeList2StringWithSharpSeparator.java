package ir.stts.bajet.core.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class JsonNodeList2StringWithSharpSeparator implements BaseConverter, AttributeConverter<List<JsonNode>, String> {

    private final static String DELIMITER = "#";

    @Override
    public String convertToDatabaseColumn(List<JsonNode> attribute) {

        if (attribute != null)
            return attribute
                    .stream()
                    .map(JsonNode::toString)
                    .collect(Collectors.joining(DELIMITER));

        return null;
    }

    @Override
    public List<JsonNode> convertToEntityAttribute(String dbData) {

        if (!StringUtils.hasText(dbData))
            return null;

        final String[] split = dbData.split(DELIMITER);
        if (ObjectUtils.isEmpty(split))
            return List.of(MAPPER.createObjectNode());

        return Arrays
                .stream(split)
                .map(str -> {

                    JsonNode jsonNode;
                    try {
                        jsonNode = MAPPER.readTree(dbData);
                    } catch (JsonProcessingException e) {
                        jsonNode = null;
                    }

                    return jsonNode;
                }).collect(Collectors.toList());
    }
}