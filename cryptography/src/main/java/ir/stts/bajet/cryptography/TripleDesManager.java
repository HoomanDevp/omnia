package ir.stts.bajet.cryptography;

import ir.stts.bajet.cryptography.config.ITripleDesProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
public class TripleDesManager {

    private static final int VALID_IV_SIZE = 8;
    private static final int[] VALID_KEY_SIZES = {128, 192};

    private SecretKey DEFAULT_KEY;
    private IvParameterSpec DEFAULT_IV;

    private final ITripleDesProperties properties;
    private final TripleDesKeyManager tripleDesKeyManager;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() throws IOException {

        String defaultIvFile = properties.getDefaultIvFile();
        if (defaultIvFile != null) {

            Optional<IvParameterSpec> optionalIv = tripleDesKeyManager.retrieveIv(defaultIvFile);
            optionalIv.ifPresent(iv -> DEFAULT_IV = iv);
        }

        String defaultKeyFile = properties.getDefaultKeyFile();
        if (defaultKeyFile != null) {

            Optional<SecretKey> optionalSecretKey = tripleDesKeyManager.retrieveSecretKey(defaultKeyFile);
            optionalSecretKey.ifPresent(secretKey -> DEFAULT_KEY = secretKey);
        }
    }

    public String encrypt(String text) throws GeneralSecurityException {
        return encrypt(text, null, null);
    }

    public String encrypt(String text, byte[] key, byte[] iv) throws GeneralSecurityException {

        IvParameterSpec ivSpec = iv == null ? DEFAULT_IV : new IvParameterSpec(iv);
        SecretKey secretKey = key == null ? DEFAULT_KEY : new SecretKeySpec(key, properties.getKeyAlgorithm());

        validateKeyAndIV(key, iv);

        Cipher cipher = Cipher.getInstance(properties.getCipherAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String encryptedText) throws GeneralSecurityException {
        return decrypt(encryptedText, null, null);
    }

    public String decrypt(String encryptedText, byte[] key, byte[] iv) throws GeneralSecurityException {

        IvParameterSpec ivSpec = iv == null ? DEFAULT_IV : new IvParameterSpec(iv);
        SecretKey secretKey = key == null ? DEFAULT_KEY : new SecretKeySpec(key, properties.getKeyAlgorithm());

        validateKeyAndIV(key, iv);

        Cipher cipher = Cipher.getInstance(properties.getCipherAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decodedCipherText = Base64.getDecoder().decode(encryptedText);
        byte[] plainText = cipher.doFinal(decodedCipherText);

        return new String(plainText, StandardCharsets.UTF_8);
    }

    private void validateKeyAndIV(byte[] key, byte[] iv) {

        if (key != null) {

            boolean validKeySize = false;
            for (int size : VALID_KEY_SIZES)
                if (key.length * 8 == size) {

                    validKeySize = true;
                    break;
                }

            if (!validKeySize)
                throw new IllegalArgumentException("Invalid key size. Supported sizes are 128 and 192 bits.");
        }

        if (iv != null && iv.length != VALID_IV_SIZE) {
            throw new IllegalArgumentException("Invalid IV size. Expected size is 8 bytes.");
        }
    }
}
