package ir.stts.bajet.cryptography;

import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.config.ISignProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ECSignManagerTest {

    private ECSignManager ecSignManager;

    @Mock
    private ISignProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getProvider()).thenReturn("BC");
        when(properties.getKeyAlgorithm()).thenReturn("EC");
        when(properties.getCurve()).thenReturn("secp256r1");
        when(properties.getAlgorithm()).thenReturn("SHA256withECDSA");
        when(properties.getKeyPairFile()).thenReturn("ir/stts/bajet/cryptography/key/ec-sign-keypair.pem");

        ResourceManager resourceManager = new ResourceManager();
        KeyManagerHelper keyManagerHelper = new KeyManagerHelper();
        ECSignKeyManager keyManager = new ECSignKeyManager(properties, resourceManager, keyManagerHelper);
        ecSignManager = new ECSignManager(properties, keyManager);
        ecSignManager.init();

        mocks.close();
    }

    @Test
    void testSignSuccess() throws Exception {

        String message = "Hello, World!";
        byte[] signature = ecSignManager.sign(message);

        assertNotNull(signature);
        assertTrue(signature.length > 0);
    }

    @Test
    void testVerifySignatureSuccess() throws Exception {

        String message = "Hello, World!";
        byte[] signature = ecSignManager.sign(message);
        boolean isValid = ecSignManager.verifySignature(message, signature);

        assertTrue(isValid);
    }

    @Test
    void testVerifySignatureFail() throws Exception {

        String message = "Hello, World!";
        String alteredMessage = "Hello, Universe!";
        byte[] signature = ecSignManager.sign(message);

        boolean isValid = ecSignManager.verifySignature(alteredMessage, signature);

        assertFalse(isValid);
    }
}