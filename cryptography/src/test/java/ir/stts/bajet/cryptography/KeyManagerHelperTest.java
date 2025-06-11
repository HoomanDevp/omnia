package ir.stts.bajet.cryptography;

import ir.stts.bajet.cryptography.config.IRsaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;

class KeyManagerHelperTest {

    private KeyManagerHelper keyManagerHelper;

    @Mock
    private IRsaProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getKeyAlgorithm()).thenReturn("RSA");
        when(properties.getKeySize()).thenReturn(2048);
        when(properties.getCipherAlgorithm()).thenReturn("RSA");
        when(properties.getDefaultKeyPairFile()).thenReturn("ir/stts/bajet/cryptography/key/rsa-keypair.pem");

        keyManagerHelper = new KeyManagerHelper();

        mocks.close();
    }

    @Test
    void testBytesToPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyPair keyPair = generateKeyPair();

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        PublicKey publicKey = keyManagerHelper.bytesToPublicKey(publicKeyBytes, properties.getKeyAlgorithm());

        assertArrayEquals(publicKeyBytes, publicKey.getEncoded());
    }

    @Test
    void testBytesToPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyPair keyPair = generateKeyPair();

        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        PrivateKey privateKey = keyManagerHelper.bytesToPrivateKey(privateKeyBytes, properties.getKeyAlgorithm());

        assertArrayEquals(privateKeyBytes, privateKey.getEncoded());
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(properties.getKeyAlgorithm());
        keyPairGenerator.initialize(properties.getKeySize());
        return keyPairGenerator.generateKeyPair();
    }
}