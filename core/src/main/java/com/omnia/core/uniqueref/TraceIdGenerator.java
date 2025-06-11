package com.omnia.core.uniqueref;

import org.springframework.stereotype.Component;

@Component
public class TraceIdGenerator extends SnowflakeIdentityGenerator {

    private volatile String id;

    public TraceIdGenerator() {
        super();
    }

    @Override
    public String latestId() {

        return this.id;
    }

    @Override
    public String generateId() {

        return this.id = super.generateId();
    }

    @Override
    public long[] parse(String id) {

        return super.parse(id);
    }
}