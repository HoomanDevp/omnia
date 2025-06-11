package ir.stts.bajet.cryptography.config;

public interface IRsaProperties {

    String getKeyAlgorithm();

    IRsaProperties setKeyAlgorithm(String keyAlgorithm);

    String getCipherAlgorithm();

    IRsaProperties setCipherAlgorithm(String cipherAlgorithm);

    int getKeySize();

    IRsaProperties setKeySize(int keySize);

    String getDefaultKeyPairFile();

    IRsaProperties setDefaultKeyPairFile(String defaultKeyPairFile);
}