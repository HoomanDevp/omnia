package com.omnia.cryptography;

import com.omnia.core.resource.ResourceManager;
import com.omnia.cryptography.config.ITripleDesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TripleDesManagerTest {

    private TripleDesManager tripleDesManager;

    @Mock
    private ITripleDesProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getKeyAlgorithm()).thenReturn("DESede");
        when(properties.getKeySize()).thenReturn(192);
        when(properties.getIvSize()).thenReturn(8);
        when(properties.getCipherAlgorithm()).thenReturn("DESede/CBC/PKCS5Padding");
        when(properties.getDefaultIvFile()).thenReturn("com//omnia/cryptography/key/tdes-default-iv.secret");
        when(properties.getDefaultKeyFile()).thenReturn("com//omnia/cryptography/key/tdes-default-key.secret");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        TripleDesKeyManager keyManager = new TripleDesKeyManager(properties, resourceManager, keyManagerHelper);
        tripleDesManager = new TripleDesManager(properties, keyManager);
        tripleDesManager.init();

        mocks.close();
    }

    @Test
    void testEncryptWithDefaultKeyAndIv() throws GeneralSecurityException {

        String text = "Hello, World!";

        String encryptedText = tripleDesManager.encrypt(text);

        assertNotNull(encryptedText);
        assertFalse(encryptedText.isEmpty());
    }

    @Test
    void testDecryptWithDefaultKeyAndIv() throws GeneralSecurityException, IOException {

        String text = "Hello, World!";

        String encryptedText = tripleDesManager.encrypt(text);
        String decryptedText = tripleDesManager.decrypt(encryptedText);

        assertNotNull(decryptedText);
        assertEquals(text, decryptedText);
    }

    @Test
    void testEncryptWithCustomKeyAndIv() throws GeneralSecurityException {

        String text = "Test Message";
        byte[] customIv = new byte[properties.getIvSize()];
        byte[] customKey = "customKeyForTesting12345".getBytes(StandardCharsets.UTF_8);

        String encryptedText = tripleDesManager.encrypt(text, customKey, customIv);

        assertNotNull(encryptedText);
        assertFalse(encryptedText.isEmpty());
    }

    @Test
    void testDecryptWithCustomKeyAndIv() throws GeneralSecurityException {

        String text = "Test Message";
        byte[] customIv = new byte[properties.getIvSize()];
        byte[] customKey = "customKeyForTesting12345".getBytes(StandardCharsets.UTF_8);

        String encryptedText = tripleDesManager.encrypt(text, customKey, customIv);
        String decryptedText = tripleDesManager.decrypt(encryptedText, customKey, customIv);

        assertNotNull(decryptedText);
        assertEquals(text, decryptedText);
    }

    @Test
    void testEncryptWithInvalidKeySize() {

        String text = "Test Message";
        byte[] invalidKey = "shortKey".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> tripleDesManager.encrypt(text, invalidKey, new byte[properties.getIvSize()]));
    }

    @Test
    void testEncryptWithInvalidIvSize() {

        String text = "Test Message";
        byte[] invalidIv = new byte[properties.getIvSize() - 4];
        byte[] customKey = "customKeyForTesting12345".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> tripleDesManager.encrypt(text, customKey, invalidIv));
    }

    @Test
    void testDecryptWithInvalidKeySize() {

        String encryptedText = "#NCRYPT3D-M3$$@G#";
        byte[] invalidKey = "shortKey".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> tripleDesManager.decrypt(encryptedText, invalidKey, new byte[properties.getIvSize()]));
    }

    @Test
    void testDecryptWithInvalidIvSize() throws GeneralSecurityException {

        String text = "Test Message";
        byte[] invalidIv = new byte[properties.getIvSize() - 4];
        byte[] customKey = "customKeyForTesting12345".getBytes(StandardCharsets.UTF_8);

        String encryptedText = tripleDesManager.encrypt(text, customKey, new byte[8]);
        assertThrows(IllegalArgumentException.class, () -> tripleDesManager.decrypt(encryptedText, customKey, invalidIv));
    }

    @Test
    void testDecryptWithInvalidData() {

        String invalidEncryptedText = "InvalidBase64Data";
        assertThrows(IllegalArgumentException.class, () -> tripleDesManager.decrypt(invalidEncryptedText));

        byte[] iv = new byte[properties.getIvSize()];
        byte[] key = "1234567890123456".getBytes(StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, () -> tripleDesManager.decrypt(invalidEncryptedText, key, iv));
    }
}