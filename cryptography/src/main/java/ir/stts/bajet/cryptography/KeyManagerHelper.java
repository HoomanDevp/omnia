package ir.stts.bajet.cryptography;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyManagerHelper {

    private static final String BEGIN_IV_KEY = "-----BEGIN IV KEY-----";
    private static final String END_IV_KEY = "-----END IV KEY-----";

    private static final String BEGIN_SECRET_KEY = "-----BEGIN SECRET KEY-----";
    private static final String END_SECRET_KEY = "-----END SECRET KEY-----";

    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    public PublicKey bytesToPublicKey(byte[] key, String keyAlgorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);

        return keyFactory.generatePublic(spec);
    }

    public PrivateKey bytesToPrivateKey(byte[] key, String keyAlgorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);

        return keyFactory.generatePrivate(spec);
    }

    void saveIv(String filename, IvParameterSpec iv) throws IOException {

        String encodedIv = Base64.getEncoder().encodeToString(iv.getIV());
        String ivContent = BEGIN_IV_KEY + "\n" + encodedIv + "\n" + END_IV_KEY;

        Files.writeString(Path.of(filename), ivContent);
    }

    IvParameterSpec loadIv(String filename) throws IOException {

        String ivContent = Files.readString(Path.of(filename));
        String encodedIv = ivContent.split(BEGIN_IV_KEY)[1]
                .split(END_IV_KEY)[0]
                .replace("\r", "")
                .replace("\n", "");

        byte[] decodedIv = Base64.getDecoder().decode(encodedIv);
        return new IvParameterSpec(decodedIv);
    }

    void saveIv(String filename, GCMParameterSpec iv) throws IOException {

        String encodedIv = Base64.getEncoder().encodeToString(iv.getIV());
        String ivContent = BEGIN_IV_KEY + "\n" + encodedIv + "\n" + END_IV_KEY;

        Files.writeString(Path.of(filename), ivContent);
    }

    GCMParameterSpec loadIv(String filename, int authTagLength) throws IOException {

        String ivContent = Files.readString(Path.of(filename));
        String encodedIv = ivContent.split(BEGIN_IV_KEY)[1]
                .split(END_IV_KEY)[0]
                .replace("\r", "")
                .replace("\n", "");

        byte[] decodedIv = Base64.getDecoder().decode(encodedIv);
        return new GCMParameterSpec(authTagLength, decodedIv);
    }

    void saveKey(String filename, SecretKey secretKey) throws IOException {

        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String keyContent = BEGIN_SECRET_KEY + "\n" + encodedKey + "\n" + END_SECRET_KEY;

        Files.writeString(Path.of(filename), keyContent);
    }

    SecretKey loadKey(String filename, String algorithm) throws IOException {

        String keyContent = Files.readString(Path.of(filename));
        String encodedKey = keyContent.split(BEGIN_SECRET_KEY)[1]
                .split(END_SECRET_KEY)[0]
                .replace("\r", "")
                .replace("\n", "");

        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, algorithm);
    }

    void saveKeyPair(String filename, KeyPair keyPair) throws IOException {

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        String keyPairContent =
                BEGIN_PUBLIC_KEY + "\n" + publicKey + "\n" + END_PUBLIC_KEY + "\n" +
                        BEGIN_PRIVATE_KEY + "\n" + privateKey + "\n" + END_PRIVATE_KEY;

        Files.writeString(Path.of(filename), keyPairContent);
    }

    KeyPair loadKeyPair(String filename, String algorithm, String provider) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        String keyPairContent = Files.readString(Path.of(filename));
        String publicKeyPEM = keyPairContent.split(END_PUBLIC_KEY)[0]
                .replace(BEGIN_PUBLIC_KEY, "")
                .replace("\r", "")
                .replace("\n", "");
        String privateKeyPEM = keyPairContent.split(END_PRIVATE_KEY)[0]
                .split(BEGIN_PRIVATE_KEY)[1]
                .replace("\r", "")
                .replace("\n", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = provider == null ? KeyFactory.getInstance(algorithm) : KeyFactory.getInstance(algorithm, provider);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        return new KeyPair(publicKey, privateKey);
    }
}
