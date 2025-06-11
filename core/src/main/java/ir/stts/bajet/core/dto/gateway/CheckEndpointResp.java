package ir.stts.bajet.core.dto.gateway;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class CheckEndpointResp {
    private Set<Endpoint> notAvailableInGateway = new HashSet<>();
    private Set<Endpoint> notAvailableInApp = new HashSet<>();
    private Set<Endpoint> notAvailableInAuth = new HashSet<>();
}
