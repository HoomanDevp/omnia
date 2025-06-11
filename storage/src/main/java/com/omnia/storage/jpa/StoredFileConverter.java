package com.omnia.storage.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Converter
@RequiredArgsConstructor
public class StoredFileConverter implements AttributeConverter<StoredFileReference, String> {

    private final static String STORED_FILE_REFERENCE_DELIMITER = "#";

    @Override
    public String convertToDatabaseColumn(StoredFileReference attribute) {

        if (attribute != null)
            return String.join(STORED_FILE_REFERENCE_DELIMITER, List.of(attribute.getBucket(), attribute.getPath(), attribute.getFileName()));

        return null;
    }

    @Override
    public StoredFileReference convertToEntityAttribute(String dbData) {

        if (StringUtils.hasText(dbData)) {

            String[] split = dbData.split(STORED_FILE_REFERENCE_DELIMITER);
            return new StoredFileReference()
                    .setBucket(split[0])
                    .setPath(split[1])
                    .setFileName(split[2]);
        }

        return null;
    }
}