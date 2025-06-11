package ir.stts.bajet.storage.dto.req;

import ir.stts.bajet.storage.dto.MinioTagDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Duration;

@Getter
@Setter
@Accessors(chain = true)
public class MinioPresignedUrlReqDto {
    @Getter(AccessLevel.NONE)
    private String path;

    @NotBlank
    @Getter(AccessLevel.NONE)
    private String name;

    @NotBlank
    private String extension;

    @NotBlank
    private String bucketName;

    @Valid
    @NotNull
    private MinioTagDto tags;

    private String versionId;

    private Duration protectionTime;

    private boolean bypassGovernanceMode = true;

    private String getPath() {

        if (path == null)
            return "";

        return path.endsWith("/") ? path : path + "/";
    }

    public String getName() {
        return String.format("%s%s.%s", getPath(), name, extension);
    }
}