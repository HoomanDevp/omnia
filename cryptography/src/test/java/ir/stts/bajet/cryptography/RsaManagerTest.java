package ir.stts.bajet.cryptography;

import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.config.IRsaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class RsaManagerTest {

    private RsaManager rsaManager;
    private RsaKeyManager keyManager;

    @Mock
    private IRsaProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getKeyAlgorithm()).thenReturn("RSA");
        when(properties.getKeySize()).thenReturn(2048);
        when(properties.getCipherAlgorithm()).thenReturn("RSA");
        when(properties.getDefaultKeyPairFile()).thenReturn("ir/stts/bajet/cryptography/key/rsa-keypair.pem");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        keyManager = new RsaKeyManager(properties, resourceManager, keyManagerHelper);
        rsaManager = new RsaManager(properties, keyManager, keyManagerHelper);
        rsaManager.init();

        mocks.close();
    }

    @Test
    void testEncryptAndDecryptSuccess() throws Exception {

        String message = "Hello, RSA!";

        String encrypted = rsaManager.encrypt(message, true);
        assertNotNull(encrypted);
        String decrypted = rsaManager.decrypt(encrypted, true);
        assertEquals(message, decrypted);

        encrypted = rsaManager.encrypt(message, false);
        assertNotNull(encrypted);
        decrypted = rsaManager.decrypt(encrypted, false);
        assertEquals(message, decrypted);
    }

    @Test
    void testEncryptAndDecryptWithCustomKeySuccess() throws Exception {

        String message = "Custom Key Test";
        KeyPair keyPair = keyManager.generateRsaKeyPair();

        String encrypted = rsaManager.encrypt(message, keyPair.getPublic());
        assertNotNull(encrypted);
        String decrypted = rsaManager.decrypt(encrypted, keyPair.getPrivate());
        assertEquals(message, decrypted);

        encrypted = rsaManager.encrypt(message, keyPair.getPrivate());
        assertNotNull(encrypted);
        decrypted = rsaManager.decrypt(encrypted, keyPair.getPublic());
        assertEquals(message, decrypted);
    }

    @Test
    void testEncryptAndDecryptWithCustomKeyByteSuccess() throws Exception {

        String message = "Custom Key Test";
        KeyPair keyPair = keyManager.generateRsaKeyPair();

        byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
        byte[] privateKeyEncoded = keyPair.getPrivate().getEncoded();

        String encrypted = rsaManager.encrypt(message, publicKeyEncoded, properties.getKeyAlgorithm(), true);
        assertNotNull(encrypted);
        String decrypted = rsaManager.decrypt(encrypted, privateKeyEncoded, properties.getKeyAlgorithm(), true);
        assertEquals(message, decrypted);

        encrypted = rsaManager.encrypt(message, privateKeyEncoded, properties.getKeyAlgorithm(), false);
        assertNotNull(encrypted);
        decrypted = rsaManager.decrypt(encrypted, publicKeyEncoded, properties.getKeyAlgorithm(), false);
        assertEquals(message, decrypted);
    }
}