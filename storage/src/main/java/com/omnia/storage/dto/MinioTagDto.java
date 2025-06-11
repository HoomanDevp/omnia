package com.omnia.storage.dto;

import com.omnia.storage.dto.constant.MinioTagKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class MinioTagDto {
    @NotBlank
    private String uploadedBy;

    @NotBlank
    private String originalName;

    @NotBlank
    private String identifier;

    @NotBlank
    private String dataIdentifier;

    @NotBlank
    private String documentIdentifier;

    @NotNull
    private int size;

    @NotBlank
    private String type;

    private boolean deleted;

    private Map<String, String> extras;

    public Map<String, String> getTagMap() {

        Map<String, String> tags = new HashMap<>();
        tags.put(MinioTagKey.UPLOADED_BY.name(), uploadedBy);
        tags.put(MinioTagKey.ORIGINAL_NAME.name(), originalName);
        tags.put(MinioTagKey.IDENTIFIER.name(), identifier);
        tags.put(MinioTagKey.DATA_IDENTIFIER.name(), dataIdentifier);
        tags.put(MinioTagKey.DOCUMENT_IDENTIFIER.name(), documentIdentifier);
        tags.put(MinioTagKey.DELETED.name(), Boolean.toString(deleted));
        tags.put(MinioTagKey.SIZE.name(), Integer.toString(size));
        tags.put(MinioTagKey.TYPE.name(), type);

        if (extras != null)
            tags.putAll(extras);

        return tags;
    }
}