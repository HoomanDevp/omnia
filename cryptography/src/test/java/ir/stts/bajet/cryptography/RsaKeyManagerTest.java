package ir.stts.bajet.cryptography;

import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.config.IRsaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RsaKeyManagerTest {

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

        mocks.close();
    }

    @Test
    void testGenerateRsaKeyPair() throws NoSuchAlgorithmException {

        KeyPair keypair = keyManager.generateRsaKeyPair();
        assertNotNull(keypair);
        assertNotNull(keypair.getPublic());
        assertNotNull(keypair.getPrivate());
    }

    @Test
    void testRetrieveRsaKeyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        var keypair = keyManager.retrieveRsaKeyPair(properties.getDefaultKeyPairFile());
        assertTrue(keypair.isPresent());
    }

    @Test
    void testGenerateAndStoreRsaKeyPair() throws Exception {

        Path tempFile = Files.createTempFile("test", ".keypair");
        tempFile.toFile().deleteOnExit();

        KeyPair keypair = keyManager.generateAndStoreRsaKeyPair(tempFile.getFileName().toString(), true);

        assertNotNull(keypair);
        assertTrue(Files.exists(tempFile));
    }
}