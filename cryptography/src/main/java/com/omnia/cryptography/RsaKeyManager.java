package com.omnia.cryptography;

import com.omnia.core.resource.ResourceManager;
import com.omnia.cryptography.config.IRsaProperties;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RequiredArgsConstructor
public class RsaKeyManager {

    private final IRsaProperties properties;
    private final ResourceManager resourceManager;
    private final KeyManagerHelper keyManagerHelper;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(properties.getKeyAlgorithm());
        keyGen.initialize(properties.getKeySize());

        return keyGen.generateKeyPair();
    }

    public Optional<KeyPair> retrieveRsaKeyPair(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        String resource = resourceManager.getResource(filename);
        if (resource != null)
            return Optional.of(keyManagerHelper.loadKeyPair(resource, properties.getKeyAlgorithm(), null));

        return Optional.empty();
    }

    public KeyPair generateAndStoreRsaKeyPair(String filename, boolean deleteIfExists) throws NoSuchAlgorithmException, IOException {

        Path keyFilePath = Path.of(resourceManager.getResource(""), filename);
        if (Files.exists(keyFilePath)) {
            if (deleteIfExists)
                Files.delete(keyFilePath);
            else
                throw new RuntimeException(keyFilePath.getFileName() + " file exists");
        }

        KeyPair keyPair = this.generateRsaKeyPair();
        keyManagerHelper.saveKeyPair(keyFilePath.toFile().getPath(), keyPair);

        return keyPair;
    }
}