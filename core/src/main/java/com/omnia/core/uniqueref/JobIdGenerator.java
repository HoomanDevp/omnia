package com.omnia.core.uniqueref;

import org.springframework.stereotype.Component;

@Component
public class JobIdGenerator extends SnowflakeIdentityGenerator {

    private volatile String id;

    public JobIdGenerator() {
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