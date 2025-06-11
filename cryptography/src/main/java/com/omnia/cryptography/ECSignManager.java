package com.omnia.cryptography;

import com.omnia.cryptography.config.ISignProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RequiredArgsConstructor
public class ECSignManager {

    private KeyPair keyPair;

    private final ISignProperties properties;
    private final ECSignKeyManager ecSignKeyManager;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        String keyPairFile = properties.getKeyPairFile();
        if (keyPairFile == null || keyPairFile.isEmpty())
            throw new RuntimeException("Key file is empty");

        Optional<KeyPair> kp = ecSignKeyManager.retrieveEcKeyPair(keyPairFile);
        if (kp.isEmpty())
            throw new RuntimeException("Key file not found");

        this.keyPair = kp.get();
    }

    public byte[] sign(String message) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {

        String provider = properties.getProvider();
        String algorithm = properties.getAlgorithm();
        Signature signature = Signature.getInstance(algorithm, provider);
        signature.initSign(keyPair.getPrivate());
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.sign();
    }

    public boolean verifySignature(String message, byte[] signatureBytes) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {

        String provider = properties.getProvider();
        String algorithm = properties.getAlgorithm();
        Signature signature = Signature.getInstance(algorithm, provider);
        signature.initVerify(keyPair.getPublic());
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signatureBytes);
    }
}