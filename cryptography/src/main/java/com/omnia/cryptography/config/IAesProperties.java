package com.omnia.cryptography.config;

public interface IAesProperties {

    String getKeyAlgorithm();

    IAesProperties setKeyAlgorithm(String keyAlgorithm);

    String getCipherAlgorithm();

    IAesProperties setCipherAlgorithm(String cipherAlgorithm);

    int getIvSize();

    IAesProperties setIvSize(int ivSize);

    int getKeySize();

    IAesProperties setKeySize(int keySize);

    int getAuthTagLength();

    IAesProperties setAuthTagLength(int authTagLength);

    String getDefaultIvFile();

    IAesProperties setDefaultIvFile(String defaultIvFile);

    String getDefaultKeyFile();

    IAesProperties setDefaultKeyFile(String defaultKeyFile);
}