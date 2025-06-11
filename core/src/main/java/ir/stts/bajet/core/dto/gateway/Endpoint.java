package ir.stts.bajet.core.dto.gateway;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class Endpoint {
    private String path;
    private String method;
    private boolean authenticated;
    private boolean bypassGateway;
    private boolean encrypted;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;
        return authenticated == endpoint.authenticated &&
                bypassGateway == endpoint.bypassGateway &&
                encrypted == endpoint.encrypted &&
                path.equals(endpoint.path) &&
                method.equals(endpoint.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method, authenticated, bypassGateway, encrypted);
    }
}
