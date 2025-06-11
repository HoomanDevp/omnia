package com.omnia.log;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogSpecTest {

    @Test
    void testLogMapWithStrings() {

        String result = LogSpec.ofMessage("Ali", "karimi", "123").toString();

        assertNotNull(result);
        assertTrue(result.contains("Ali karimi 123"));
    }

    @Test
    void testLogMapWithCustomDelimiter() {

        String result = LogSpec.ofMessage(',', "Ali", "karimi", "123").toString();

        assertNotNull(result);
        assertTrue(result.contains("Ali,karimi,123"));
    }

    @Test
    void testLogMapWithDataObject() {

        Data data = new Data()
                .setId(1)
                .setAge(30)
                .setFirstname("Ali")
                .setLastname("Karimi");

        String result = LogSpec.ofData("", data).toString();

        assertNotNull(result);
        assertTrue(result.contains("\"id\":1"));
        assertTrue(result.contains("\"firstname\":\"Ali\""));
    }

    @Test
    void testLogMapWithMapEntries() {

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", 100);
        dataMap.put("name", "AliKarimi");
        dataMap.put("isDeveloper", true);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String result = LogSpec.ofMap("", dataMap).toString();
        stopWatch.stop();

        assertNotNull(result);
        assertTrue(result.contains("\"id\":100"));
        assertTrue(result.contains("\"name\":\"AliKarimi\""));
    }

    @Test
    void testLogMapWithExceptions() {

        String result = LogSpec.ofException("", new RuntimeException("This is test")).toString();

        assertNotNull(result);
        assertTrue(result.contains("This is test"));
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    private static class Data {

        private int id;
        private int age;
        private String firstname;
        private String lastname;
    }
}