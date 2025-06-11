package com.omnia.cryptography.config;

public interface ITripleDesProperties {

    String getKeyAlgorithm();

    ITripleDesProperties setKeyAlgorithm(String keyAlgorithm);

    String getCipherAlgorithm();

    ITripleDesProperties setCipherAlgorithm(String cipherAlgorithm);

    int getIvSize();

    ITripleDesProperties setIvSize(int ivSize);

    int getKeySize();

    ITripleDesProperties setKeySize(int keySize);

    String getDefaultIvFile();

    ITripleDesProperties setDefaultIvFile(String defaultIvFile);

    String getDefaultKeyFile();

    ITripleDesProperties setDefaultKeyFile(String defaultKeyFile);
}