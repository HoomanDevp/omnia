package ir.stts.bajet.storage.dto.resp;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MinioDownloadRespDto {

    private byte[] file;
    private boolean deleted;
}