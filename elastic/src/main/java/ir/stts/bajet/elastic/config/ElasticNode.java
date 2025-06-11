package ir.stts.bajet.elastic.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ElasticNode {

    String ip;
    Integer port;

    public String toAddress() {
        return ip + ":" + port;
    }
}