package ir.stts.bajet.cryptography;

import ir.stts.bajet.cryptography.config.IAesProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
public class AesManager {

    private static final int VALID_IV_SIZE = 12;
    private static final int[] VALID_KEY_SIZES = {128, 192, 256};

    private SecretKey DEFAULT_KEY;
    private GCMParameterSpec DEFAULT_IV;

    private final IAesProperties properties;
    private final AesKeyManager aesKeyManager;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() throws IOException {

        String defaultIvFile = properties.getDefaultIvFile();
        if (defaultIvFile != null) {

            Optional<GCMParameterSpec> optionalIv = aesKeyManager.retrieveIv(defaultIvFile);
            optionalIv.ifPresent(iv -> DEFAULT_IV = iv);
        }

        String defaultKeyFile = properties.getDefaultKeyFile();
        if (defaultKeyFile != null) {

            Optional<SecretKey> optionalSecretKey = aesKeyManager.retrieveSecretKey(defaultKeyFile);
            optionalSecretKey.ifPresent(secretKey -> DEFAULT_KEY = secretKey);
        }
    }

    public String encrypt(String text) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        return encrypt(text, null, null);
    }

    public String encrypt(String text, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {

        GCMParameterSpec ivSpec = iv == null ? DEFAULT_IV : new GCMParameterSpec(properties.getAuthTagLength(), iv);
        SecretKey secretKey = key == null ? DEFAULT_KEY : new SecretKeySpec(key, properties.getKeyAlgorithm());

        validateKeyAndIV(key, iv);

        Cipher cipher = Cipher.getInstance(properties.getCipherAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String encryptedText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        return decrypt(encryptedText, null, null);
    }

    public String decrypt(String encryptedText, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        GCMParameterSpec ivSpec = iv == null ? DEFAULT_IV : new GCMParameterSpec(properties.getAuthTagLength(), iv);
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
                throw new IllegalArgumentException("Invalid key size. Supported sizes are 128, 192, or [256] bits.");
        }

        if (iv != null && iv.length != VALID_IV_SIZE)
            throw new IllegalArgumentException("Invalid IV size. Expected size is 12 bytes.");
    }
}