package com.omnia.core;

import com.omnia.core.uniqueref.SnowflakeIdentityGenerator;
import com.omnia.core.uniqueref.TraceIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.NetworkInterface;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TraceIdGeneratorTest {

    private TraceIdGenerator traceIdGenerator;

    @BeforeEach
    void setUp() {
        traceIdGenerator = new TraceIdGenerator();
    }

    @Test
    void testGenerateId() {

        String id1 = traceIdGenerator.generateId();
        String id2 = traceIdGenerator.generateId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void testLatestId() {

        String id = traceIdGenerator.generateId();
        assertEquals(id, traceIdGenerator.latestId());
    }

    @Test
    void testParseValidId() {

        String generatedId = traceIdGenerator.generateId();
        long[] parsedId = traceIdGenerator.parse(generatedId);

        assertNotNull(parsedId);
        assertEquals(3, parsedId.length);
        assertTrue(parsedId[0] > 0);
        assertTrue(parsedId[1] >= 0);
        assertTrue(parsedId[2] >= 0);
    }

    @Test
    void testParseInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> traceIdGenerator.parse("invalidHex"));
    }

    @Test
    void testUniqueIdGeneration() {

        String id1 = traceIdGenerator.generateId();
        String id2 = traceIdGenerator.generateId();
        String id3 = traceIdGenerator.generateId();

        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
    }

    @Test
    void testNodeIdGenerationWithValidMacAddress() throws Exception {

        NetworkInterface mockNetworkInterface = mock(NetworkInterface.class);
        when(mockNetworkInterface.isUp()).thenReturn(true);
        when(mockNetworkInterface.isLoopback()).thenReturn(false);
        byte[] macAddress = {(byte) 0x00, (byte) 0x14, (byte) 0x22, (byte) 0x01, (byte) 0x35, (byte) 0x66};
        when(mockNetworkInterface.getHardwareAddress()).thenReturn(macAddress);

        //noinspection unchecked
        Enumeration<NetworkInterface> networkInterfaces = mock(Enumeration.class);
        when(networkInterfaces.hasMoreElements()).thenReturn(true, false);
        when(networkInterfaces.nextElement()).thenReturn(mockNetworkInterface);

        SnowflakeIdentityGenerator snowflakeIdentityGenerator = new TraceIdGenerator();
        String id = snowflakeIdentityGenerator.generateId();

        long[] parsedId = snowflakeIdentityGenerator.parse(id);
        assertTrue(parsedId[1] >= 0);
    }

    @Test
    void testNodeIdGenerationWithRandomValue() {

        SnowflakeIdentityGenerator snowflakeIdentityGenerator = new TraceIdGenerator();
        String id = snowflakeIdentityGenerator.generateId();

        long[] parsedId = snowflakeIdentityGenerator.parse(id);
        assertTrue(parsedId[1] >= 0);
    }
}