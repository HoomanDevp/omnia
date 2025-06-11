package com.omnia.core.uniqueref;

public interface ISnowflakeIdentityGenerator {

    String latestId();

    String generateId();

    long[] parse(String id);
}