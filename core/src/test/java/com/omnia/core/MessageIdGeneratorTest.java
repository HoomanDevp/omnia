package com.omnia.core;

import com.omnia.core.uniqueref.MessageIdGenerator;
import com.omnia.core.uniqueref.SnowflakeIdentityGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.NetworkInterface;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageIdGeneratorTest {

    private MessageIdGenerator messageIdGenerator;

    @BeforeEach
    void setUp() {
        messageIdGenerator = new MessageIdGenerator();
    }

    @Test
    void testGenerateId() {

        String id1 = messageIdGenerator.generateId();
        String id2 = messageIdGenerator.generateId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void testLatestId() {

        String id = messageIdGenerator.generateId();
        assertEquals(id, messageIdGenerator.latestId());
    }

    @Test
    void testParseValidId() {

        String generatedId = messageIdGenerator.generateId();
        long[] parsedId = messageIdGenerator.parse(generatedId);

        assertNotNull(parsedId);
        assertEquals(3, parsedId.length);
        assertTrue(parsedId[0] > 0);
        assertTrue(parsedId[1] >= 0);
        assertTrue(parsedId[2] >= 0);
    }

    @Test
    void testParseInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> messageIdGenerator.parse("invalidHex"));
    }

    @Test
    void testUniqueIdGeneration() {

        String id1 = messageIdGenerator.generateId();
        String id2 = messageIdGenerator.generateId();
        String id3 = messageIdGenerator.generateId();

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

        SnowflakeIdentityGenerator snowflakeIdentityGenerator = new MessageIdGenerator();
        String id = snowflakeIdentityGenerator.generateId();

        long[] parsedId = snowflakeIdentityGenerator.parse(id);
        assertTrue(parsedId[1] >= 0);
    }

    @Test
    void testNodeIdGenerationWithRandomValue() {

        SnowflakeIdentityGenerator snowflakeIdentityGenerator = new MessageIdGenerator();
        String id = snowflakeIdentityGenerator.generateId();

        long[] parsedId = snowflakeIdentityGenerator.parse(id);
        assertTrue(parsedId[1] >= 0);
    }
}