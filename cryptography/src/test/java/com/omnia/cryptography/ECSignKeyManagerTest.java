package com.omnia.cryptography;

import com.omnia.core.resource.ResourceManager;
import com.omnia.cryptography.config.ISignProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ECSignKeyManagerTest {

    private ECSignKeyManager ecSignKeyManager;

    @Mock
    private ISignProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getProvider()).thenReturn("BC");
        when(properties.getKeyAlgorithm()).thenReturn("EC");
        when(properties.getCurve()).thenReturn("secp256r1");
        when(properties.getAlgorithm()).thenReturn("SHA256withECDSA");
        when(properties.getKeyPairFile()).thenReturn("com//omnia/cryptography/key/ec-sign-keypair.pem");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        ecSignKeyManager = new ECSignKeyManager(properties, resourceManager, keyManagerHelper);

        mocks.close();
    }

    @Test
    void testGenerateEcKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

        KeyPair keypair = ecSignKeyManager.generateEcKeyPair();
        assertNotNull(keypair);
        assertNotNull(keypair.getPublic());
        assertNotNull(keypair.getPrivate());
    }

    @Test
    void testRetrieveEcKeyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        var keypair = ecSignKeyManager.retrieveEcKeyPair(properties.getKeyPairFile());
        assertTrue(keypair.isPresent());
    }

    @Test
    void testGenerateAndStoreEcKeyPair() throws Exception {

        Path tempFile = Files.createTempFile("test", ".ec.keypair");
        tempFile.toFile().deleteOnExit();

        KeyPair keypair = ecSignKeyManager.generateAndStoreEcKeyPair(tempFile.getFileName().toString(), true);

        assertNotNull(keypair);
        assertTrue(Files.exists(tempFile));
    }
}