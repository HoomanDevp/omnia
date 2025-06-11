package ir.stts.bajet.storage.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Converter
public class CollectionStoredFileConverter implements AttributeConverter<Collection<StoredFileReference>, String> {

    private static final String COLLECTION_DELIMITER = ";";
    private static final String STORED_FILE_REFERENCE_DELIMITER = "#";

    @Override
    public String convertToDatabaseColumn(Collection<StoredFileReference> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        return attribute.stream()
                .map(file -> String.join(STORED_FILE_REFERENCE_DELIMITER,
                        (file.getBucket()),
                        (file.getPath()),
                        (file.getFileName()),
                        (file.getFileExtension())))
                .collect(Collectors.joining(COLLECTION_DELIMITER));
    }

    @Override
    public Collection<StoredFileReference> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData)) {
            return Collections.emptyList();
        }

        List<StoredFileReference> references = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(dbData, COLLECTION_DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            String[] parts = tokenizer.nextToken().split(STORED_FILE_REFERENCE_DELIMITER);

            // extension is optional
            StoredFileReference ref = new StoredFileReference()
                    .setBucket(parts[0])
                    .setPath(parts[1])
                    .setFileName(parts[2])
                    .setFileExtension(parts[3]);

            references.add(ref);
        }
        return references;
    }

}