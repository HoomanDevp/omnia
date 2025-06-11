package ir.stts.bajet.elastic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.stts.bajet.log.LogSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

@Slf4j
public class ElasticQueryLogger implements HttpRequestInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void process(HttpRequest request, HttpContext context) throws IOException {

        if (request instanceof HttpEntityEnclosingRequest sub)
            if (sub.getEntity() != null)
                log.info("{}", LogSpec.ofMessage(
                        "Elastic Query on [",
                        request.getRequestLine().toString(),
                        "] body is [",
                        mapper
                                .readTree(sub
                                        .getEntity()
                                        .getContent())
                                .toPrettyString(),
                        "]"));
    }
}