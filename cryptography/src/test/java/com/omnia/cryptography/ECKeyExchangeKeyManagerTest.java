package com.omnia.cryptography;

import com.omnia.cryptography.config.IKeyExchangeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class ECKeyExchangeKeyManagerTest {

    private ECKeyExchangeKeyManager ecKeyExchangeKeyManager;

    @Mock
    private IKeyExchangeProperties properties;

    @BeforeEach
    void setUp() throws Exception {

        AutoCloseable mocks = MockitoAnnotations.openMocks(this);

        when(properties.getProvider()).thenReturn("BC");
        when(properties.getKeyAlgorithm()).thenReturn("EC");
        when(properties.getCurve()).thenReturn("secp256r1");
        when(properties.getAlgorithm()).thenReturn("ECDH");

        ecKeyExchangeKeyManager = new ECKeyExchangeKeyManager(properties);

        mocks.close();
    }

    @Test
    void testGenerateEcKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

        KeyPair keypair = ecKeyExchangeKeyManager.generateEcKeyPair();
        assertNotNull(keypair);
        assertNotNull(keypair.getPublic());
        assertNotNull(keypair.getPrivate());
    }
}