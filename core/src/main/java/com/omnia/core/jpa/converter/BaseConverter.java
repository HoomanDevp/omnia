package com.omnia.core.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface BaseConverter {

    ObjectMapper MAPPER = new ObjectMapper();
}