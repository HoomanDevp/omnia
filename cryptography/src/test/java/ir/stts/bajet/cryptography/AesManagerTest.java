package ir.stts.bajet.cryptography;

import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.config.IAesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AesManagerTest {

    private AesManager aesManager;

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
        when(properties.getDefaultIvFile()).thenReturn("ir/stts/bajet/cryptography/key/aes-default-iv.secret");
        when(properties.getDefaultKeyFile()).thenReturn("ir/stts/bajet/cryptography/key/aes-default-key.secret");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        AesKeyManager keyManager = new AesKeyManager(properties, resourceManager, keyManagerHelper);
        aesManager = new AesManager(properties, keyManager);
        aesManager.init();

        mocks.close();
    }

    @Test
    void testEncryptWithDefaultKeyAndIv() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        String text = "Hello, World!";

        String encryptedText = aesManager.encrypt(text);

        assertNotNull(encryptedText);
        assertFalse(encryptedText.isEmpty());
    }

    @Test
    void testDecryptWithDefaultKeyAndIv() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

        String text = "Hello, World!";

        String encryptedText = aesManager.encrypt(text);
        String decryptedText = aesManager.decrypt(encryptedText);

        assertNotNull(decryptedText);
        assertEquals(text, decryptedText);
    }

    @Test
    void testEncryptWithCustomKeyAndIv() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {

        String text = "Test Message";
        byte[] customIv = new byte[properties.getIvSize()];
        byte[] customKey = "customKeyForTesting1234567890000".getBytes(StandardCharsets.UTF_8);

        String encryptedText = aesManager.encrypt(text, customKey, customIv);

        assertNotNull(encryptedText);
        assertFalse(encryptedText.isEmpty());
    }

    @Test
    void testDecryptWithCustomKeyAndIv() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        String text = "Test Message";
        byte[] customIv = new byte[properties.getIvSize()];
        byte[] customKey = "customKeyForTesting1234567890000".getBytes(StandardCharsets.UTF_8);

        String encryptedText = aesManager.encrypt(text, customKey, customIv);
        String decryptedText = aesManager.decrypt(encryptedText, customKey, customIv);

        assertNotNull(decryptedText);
        assertEquals(text, decryptedText);
    }

    @Test
    void testEncryptWithInvalidKeySize() {

        String text = "Test Message";
        byte[] invalidKey = "shortKey".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> aesManager.encrypt(text, invalidKey, new byte[properties.getIvSize()]));
    }

    @Test
    void testEncryptWithInvalidIvSize() {

        String text = "Test Message";
        byte[] invalidIv = new byte[properties.getIvSize() - 4];
        byte[] customKey = "customKeyForTesting1234567890000".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> aesManager.encrypt(text, customKey, invalidIv));
    }

    @Test
    void testDecryptWithInvalidKeySize() {

        String encryptedText = "#NCRYPT3D-M3$$@G#";
        byte[] invalidKey = "shortKey".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> aesManager.decrypt(encryptedText, invalidKey, new byte[properties.getIvSize()]));
    }

    @Test
    void testDecryptWithInvalidIvSize() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {

        String text = "Test Message";
        byte[] invalidIv = new byte[properties.getIvSize() - 4];
        byte[] customKey = "customKeyForTesting1234567890000".getBytes(StandardCharsets.UTF_8);

        String encryptedText = aesManager.encrypt(text, customKey, new byte[12]);
        assertThrows(IllegalArgumentException.class, () -> aesManager.decrypt(encryptedText, customKey, invalidIv));
    }

    @Test
    void testDecryptWithInvalidData() {

        String invalidEncryptedText = "InvalidBase64Data";
        assertThrows(IllegalArgumentException.class, () -> aesManager.decrypt(invalidEncryptedText));

        byte[] iv = new byte[properties.getIvSize()];
        byte[] key = "1234567890123456".getBytes(StandardCharsets.UTF_8);
        assertThrows(IllegalArgumentException.class, () -> aesManager.decrypt(invalidEncryptedText, key, iv));
    }
}