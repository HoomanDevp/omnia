package ir.stts.bajet.core.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class CheckEndpointReq {
    private String appKey;
    private Set<String> maskKeys = new HashSet<>();
    private List<Endpoint> endpoints = new ArrayList<>();
}
