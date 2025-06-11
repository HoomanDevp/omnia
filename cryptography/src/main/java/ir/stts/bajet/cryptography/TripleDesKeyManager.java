package ir.stts.bajet.cryptography;

import ir.stts.bajet.core.resource.ResourceManager;
import ir.stts.bajet.cryptography.config.ITripleDesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Optional;

@RequiredArgsConstructor
public class TripleDesKeyManager {

    private final ITripleDesProperties properties;
    private final ResourceManager resourceManager;
    private final KeyManagerHelper keyManagerHelper;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public SecretKey generateSecretKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGen = KeyGenerator.getInstance(properties.getKeyAlgorithm());
        keyGen.init(properties.getKeySize());

        return keyGen.generateKey();
    }

    public Optional<SecretKey> retrieveSecretKey(String filename) throws IOException {

        String resource = resourceManager.getResource(filename);
        if (resource != null)
            return Optional.of(keyManagerHelper.loadKey(resource, properties.getKeyAlgorithm()));

        return Optional.empty();
    }

    public SecretKey generateAndStoreSecretKey(String filename, boolean deleteIfExists) throws Exception {

        Path keyFilePath = Path.of(resourceManager.getResource(""), filename);
        if (Files.exists(keyFilePath)) {
            if (deleteIfExists)
                Files.delete(keyFilePath);
            else
                throw new RuntimeException(keyFilePath.getFileName() + " file exists");
        }

        SecretKey secretKey = this.generateSecretKey();
        keyManagerHelper.saveKey(keyFilePath.toFile().getPath(), secretKey);

        return secretKey;
    }

    public IvParameterSpec generateIv() {

        byte[] iv = new byte[properties.getIvSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        return new IvParameterSpec(iv);
    }

    public Optional<IvParameterSpec> retrieveIv(String filename) throws IOException {

        String resource = resourceManager.getResource(filename);
        if (resource != null)
            return Optional.of(keyManagerHelper.loadIv(resource));

        return Optional.empty();
    }

    public IvParameterSpec generateAndStoreIv(String filename, boolean deleteIfExists) throws Exception {

        Path keyFilePath = Path.of(resourceManager.getResource(""), filename);
        if (Files.exists(keyFilePath)) {
            if (deleteIfExists)
                Files.delete(keyFilePath);
            else
                throw new RuntimeException(keyFilePath.getFileName() + " file exists");
        }

        IvParameterSpec iv = this.generateIv();
        keyManagerHelper.saveIv(keyFilePath.toFile().getPath(), iv);

        return iv;
    }
}
