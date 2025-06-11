package com.omnia.cryptography;

import com.omnia.cryptography.config.IRsaProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
public class RsaManager {

    private static final int MIN_RSA_KEY_SIZE = 1024;
    private static final int MAX_RSA_KEY_SIZE = 4096;

    private KeyPair DEFAULT_KEYPAIR;

    private final IRsaProperties properties;
    private final RsaKeyManager rsaKeyManager;
    private final KeyManagerHelper keyManagerHelper;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        String defaultKeyPairFile = properties.getDefaultKeyPairFile();
        if (defaultKeyPairFile != null) {

            Optional<KeyPair> optionalKeypair = rsaKeyManager.retrieveRsaKeyPair(defaultKeyPairFile);
            optionalKeypair.ifPresent(keyPair -> DEFAULT_KEYPAIR = keyPair);
        }
    }

    public String encrypt(String text, boolean withPublic) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        return encrypt(text, withPublic ? DEFAULT_KEYPAIR.getPublic() : DEFAULT_KEYPAIR.getPrivate());
    }

    public String encrypt(String text, byte[] key, String algorithm, boolean isPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        return encrypt(text, isPublicKey ? keyManagerHelper.bytesToPublicKey(key, algorithm) : keyManagerHelper.bytesToPrivateKey(key, algorithm));
    }

    public String encrypt(String text, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        validateKey(key);

        Cipher cipher = Cipher.getInstance(properties.getCipherAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String encryptedText, boolean withPrivate) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        return decrypt(encryptedText, withPrivate ? DEFAULT_KEYPAIR.getPrivate() : DEFAULT_KEYPAIR.getPublic());
    }

    public String decrypt(String encryptedText, byte[] key, String algorithm, boolean isPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        return decrypt(encryptedText, isPrivateKey ? keyManagerHelper.bytesToPrivateKey(key, algorithm) : keyManagerHelper.bytesToPublicKey(key, algorithm));
    }

    public String decrypt(String encryptedText, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        validateKey(key);

        Cipher cipher = Cipher.getInstance(properties.getCipherAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

        return new String(plainText, StandardCharsets.UTF_8);
    }

    private void validateKey(Key key) {

        if (!(key instanceof RSAKey))
            throw new IllegalArgumentException("Invalid key.");

        int keySize = ((RSAKey) key).getModulus().bitLength();
        if (keySize >= MIN_RSA_KEY_SIZE &&
                keySize <= MAX_RSA_KEY_SIZE &&
                keySize % 256 == 0)
            return;

        throw new IllegalArgumentException("Invalid key size. Supported sizes are 1024, [2048], [3072] or 4096 bits.");
    }
}