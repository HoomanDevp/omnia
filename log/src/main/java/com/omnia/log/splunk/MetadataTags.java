package com.omnia.log.splunk;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MetadataTags {
    public static final String TIME = "time";
    public static final String HOST = "host";
    public static final String INDEX = "index";
    public static final String SOURCE = "source";
    public static final String SOURCETYPE = "sourcetype";
    public static final String MESSAGEFORMAT = "messageFormat";
    public static final Set<String> HEC_TAGS = new HashSet(Arrays.asList("time", "host", "index", "source", "sourcetype"));
    public static final Set<String> INTERNAL_TAGS = new HashSet(Collections.singletonList("messageFormat"));

    public MetadataTags() {
    }
}
