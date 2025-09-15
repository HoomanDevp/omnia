package com.omnia.cryptography;

import com.omnia.core.resource.ResourceManager;
import com.omnia.cryptography.config.IAesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AesKeyManagerTest {

    private AesKeyManager aesKeyManager;

    @Mock
    private IAesProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getKeyAlgorithm()).thenReturn("AES");
        when(properties.getKeySize()).thenReturn(256);
        when(properties.getIvSize()).thenReturn(12);
        when(properties.getAuthTagLength()).thenReturn(128);
        when(properties.getCipherAlgorithm()).thenReturn("AES/GCM/NoPadding");
        when(properties.getDefaultIvFile()).thenReturn("com//omnia/cryptography/key/aes-default-iv.secret");
        when(properties.getDefaultKeyFile()).thenReturn("com//omnia/cryptography/key/aes-default-key.secret");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        aesKeyManager = new AesKeyManager(properties, resourceManager, keyManagerHelper);

        mocks.close();
    }

    @Test
    void testGenerateSecretKey() throws NoSuchAlgorithmException {

        SecretKey secretKey = aesKeyManager.generateSecretKey();
        assertNotNull(secretKey);
        assertEquals(properties.getKeyAlgorithm(), secretKey.getAlgorithm());
    }

    @Test
    void testGenerateIv() {

        GCMParameterSpec iv = aesKeyManager.generateIv();
        assertNotNull(iv);
        assertEquals(properties.getIvSize(), iv.getIV().length);
        assertEquals(properties.getAuthTagLength(), iv.getTLen());
    }

    @Test
    void testRetrieveSecretKey() throws IOException {

        var secretKey = aesKeyManager.retrieveSecretKey(properties.getDefaultKeyFile());
        assertTrue(secretKey.isPresent());
    }

    @Test
    void testRetrieveIv() throws IOException {

        var iv = aesKeyManager.retrieveIv(properties.getDefaultIvFile());
        assertTrue(iv.isPresent());
    }

    @Test
    void testGenerateAndStoreSecretKey() throws Exception {

        Path tempFile = Files.createTempFile("test", ".key");
        tempFile.toFile().deleteOnExit();

        SecretKey secretKey = aesKeyManager.generateAndStoreSecretKey(tempFile.getFileName().toString(), true);

        assertNotNull(secretKey);
        assertTrue(Files.exists(tempFile));
    }

    @Test
    void testGenerateAndStoreIv() throws Exception {

        Path tempFile = Files.createTempFile("test", ".iv");
        tempFile.toFile().deleteOnExit();

        GCMParameterSpec iv = aesKeyManager.generateAndStoreIv(tempFile.getFileName().toString(), true);

        assertNotNull(iv);
        assertTrue(Files.exists(tempFile));
    }
}