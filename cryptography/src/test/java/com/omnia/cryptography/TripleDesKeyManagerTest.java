package com.omnia.cryptography;

import com.omnia.core.resource.ResourceManager;
import com.omnia.cryptography.config.ITripleDesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TripleDesKeyManagerTest {

    private TripleDesKeyManager tripleDesKeyManager;

    @Mock
    private ITripleDesProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getKeyAlgorithm()).thenReturn("DESede");
        when(properties.getIvSize()).thenReturn(8);
        when(properties.getKeySize()).thenReturn(192);
        when(properties.getCipherAlgorithm()).thenReturn("DESede/CBC/PKCS5Padding");
        when(properties.getDefaultIvFile()).thenReturn("com//omnia/cryptography/key/tdes-default-iv.secret");
        when(properties.getDefaultKeyFile()).thenReturn("com//omnia/cryptography/key/tdes-default-key.secret");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        tripleDesKeyManager = new TripleDesKeyManager(properties, resourceManager, keyManagerHelper);

        mocks.close();
    }

    @Test
    void testGenerateSecretKey() throws NoSuchAlgorithmException {

        SecretKey secretKey = tripleDesKeyManager.generateSecretKey();
        assertNotNull(secretKey);
        assertEquals(properties.getKeyAlgorithm(), secretKey.getAlgorithm());
    }

    @Test
    void testGenerateIv() {

        IvParameterSpec iv = tripleDesKeyManager.generateIv();
        assertNotNull(iv);
        assertEquals(properties.getIvSize(), iv.getIV().length);
    }

    @Test
    void testRetrieveSecretKey() throws IOException {

        var secretKey = tripleDesKeyManager.retrieveSecretKey(properties.getDefaultKeyFile());
        assertTrue(secretKey.isPresent());
    }

    @Test
    void testRetrieveIv() throws IOException {

        var iv = tripleDesKeyManager.retrieveIv(properties.getDefaultIvFile());
        assertTrue(iv.isPresent());
    }

    @Test
    void testGenerateAndStoreSecretKey() throws Exception {

        Path tempFile = Files.createTempFile("test", ".key");
        tempFile.toFile().deleteOnExit();

        SecretKey secretKey = tripleDesKeyManager.generateAndStoreSecretKey(tempFile.getFileName().toString(), true);

        assertNotNull(secretKey);
        assertTrue(Files.exists(tempFile));
    }

    @Test
    void testGenerateAndStoreIv() throws Exception {

        Path tempFile = Files.createTempFile("test", ".iv");
        tempFile.toFile().deleteOnExit();

        IvParameterSpec iv = tripleDesKeyManager.generateAndStoreIv(tempFile.getFileName().toString(), true);

        assertNotNull(iv);
        assertTrue(Files.exists(tempFile));
    }
}