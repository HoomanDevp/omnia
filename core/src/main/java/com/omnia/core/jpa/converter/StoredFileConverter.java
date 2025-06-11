package com.omnia.core.jpa.converter;

import com.omnia.core.dto.StoredFileDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Converter
@RequiredArgsConstructor
public class StoredFileConverter implements AttributeConverter<StoredFileDto, String> {

    private final static String DELIMITER = "#";

    @Override
    public String convertToDatabaseColumn(StoredFileDto attribute) {

        if (attribute != null)
            return String.join(DELIMITER, List.of(attribute.getBucket(), attribute.getPath(), attribute.getFileName(), attribute.getFileExtension()));

        return null;
    }

    @Override
    public StoredFileDto convertToEntityAttribute(String dbData) {

        if (StringUtils.hasText(dbData)) {

            String[] split = dbData.split(DELIMITER);
            return new StoredFileDto()
                    .setBucket(split[0])
                    .setPath(split[1])
                    .setFileName(split[2])
                    .setFileExtension(split[3]);
        }

        return null;
    }
}