package ir.stts.bajet.elastic.log;

import ir.stts.bajet.elastic.index.AbstractIndexService;
import ir.stts.bajet.elastic.index.strategy.IndexStrategy;

public class AppLogIndexService extends AbstractIndexService {

    private final String prefix;

    public AppLogIndexService(String separator, IndexStrategy indexStrategy, String prefix) {
        super(separator, indexStrategy);
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}