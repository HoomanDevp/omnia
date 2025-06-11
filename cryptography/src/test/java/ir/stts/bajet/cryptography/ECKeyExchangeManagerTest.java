package ir.stts.bajet.cryptography;

import ir.stts.bajet.cryptography.config.IKeyExchangeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ECKeyExchangeManagerTest {

    private ECKeyExchangeManager ecKeyExchangeManager;

    @Mock
    private IKeyExchangeProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getProvider()).thenReturn("BC");
        when(properties.getKeyAlgorithm()).thenReturn("EC");
        when(properties.getCurve()).thenReturn("secp256r1");
        when(properties.getAlgorithm()).thenReturn("ECDH");

        ECKeyExchangeKeyManager keyManager = new ECKeyExchangeKeyManager(properties);
        ecKeyExchangeManager = new ECKeyExchangeManager(properties, keyManager);
        ecKeyExchangeManager.init();

        mocks.close();
    }

    @Test
    void testGenerateServerPublicKey() {

        byte[] publicKeyBytes = ecKeyExchangeManager.generateServerPublicKey();

        assertNotNull(publicKeyBytes);
        assertTrue(publicKeyBytes.length > 0);
    }

    @Test
    void testDerivePublicKeyFromBytes() throws Exception {

        byte[] publicKeyBytes = ecKeyExchangeManager.generateServerPublicKey();
        PublicKey publicKey = ecKeyExchangeManager.derivePublicKeyFromBytes(publicKeyBytes);

        assertNotNull(publicKey);
        assertEquals("ECDH", publicKey.getAlgorithm());
    }

    @Test
    void testDerivePublicKeyFromBytesWithInvalidFormat() {

        byte[] invalidKey = new byte[]{0x00, 0x01, 0x02};

        assertThrows(IllegalArgumentException.class, () -> {
            ecKeyExchangeManager.derivePublicKeyFromBytes(invalidKey);
        });
    }

    @Test
    void testGenerateSecretKey() throws Exception {

        byte[] serverPublicKeyBytes = ecKeyExchangeManager.generateServerPublicKey();
        PublicKey serverPublicKey = ecKeyExchangeManager.derivePublicKeyFromBytes(serverPublicKeyBytes);
        byte[] serverSecretKey = ecKeyExchangeManager.generateSecretKey(serverPublicKey);

        byte[] clientPublicKeyBytes = ecKeyExchangeManager.generateServerPublicKey();
        PublicKey clientPublicKey = ecKeyExchangeManager.derivePublicKeyFromBytes(clientPublicKeyBytes);
        byte[] clientSecretKey = ecKeyExchangeManager.generateSecretKey(clientPublicKey);

        assertNotNull(clientSecretKey);
        assertTrue(clientSecretKey.length > 0);
        assertTrue(ecKeyExchangeManager.checkSecretKey(clientSecretKey, serverSecretKey));
    }
}