package ir.stts.bajet.core.header.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HeaderSpec {

    private String lang;
    private String msgId;
    private String jobId;
    private UserInfo userInfo;
    private ClientInfo clientInfo;
    private Map<String, String> headers = new HashMap<>();
}
