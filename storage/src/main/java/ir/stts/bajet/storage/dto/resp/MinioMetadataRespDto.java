package ir.stts.bajet.storage.dto.resp;

import ir.stts.bajet.storage.dto.MinioTagDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class MinioMetadataRespDto {

    private long size;
    private String etag;
    private boolean legalHold;
    private boolean deleteMarker;
    private String retentionMode;
    private ZonedDateTime retentionRetainUntilDate;
    private ZonedDateTime lastModified;

    private MinioTagDto tags;
}