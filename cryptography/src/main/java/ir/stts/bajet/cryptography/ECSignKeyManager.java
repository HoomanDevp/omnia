package ir.stts.bajet.cryptography;

import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.config.ISignProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RequiredArgsConstructor
public class ECSignKeyManager {

    private final ISignProperties properties;
    private final ResourceManager resourceManager;
    private final KeyManagerHelper keyManagerHelper;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyPair generateEcKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(properties.getKeyAlgorithm(), properties.getProvider());
        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(properties.getCurve());
        keyGen.initialize(parameterSpec);

        return keyGen.generateKeyPair();
    }

    public Optional<KeyPair> retrieveEcKeyPair(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        String resource = resourceManager.getResource(filename);
        if (resource != null)
            return Optional.of(keyManagerHelper.loadKeyPair(resource, properties.getKeyAlgorithm(), properties.getProvider()));

        return Optional.empty();
    }

    public KeyPair generateAndStoreEcKeyPair(String filename, boolean deleteIfExists) throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        Path keyFilePath = Path.of(resourceManager.getResource(""), filename);
        if (Files.exists(keyFilePath)) {
            if (deleteIfExists)
                Files.delete(keyFilePath);
            else
                throw new RuntimeException(keyFilePath.getFileName() + " file exists");
        }

        KeyPair keyPair = this.generateEcKeyPair();
        keyManagerHelper.saveKeyPair(keyFilePath.toFile().getPath(), keyPair);

        return keyPair;
    }
}