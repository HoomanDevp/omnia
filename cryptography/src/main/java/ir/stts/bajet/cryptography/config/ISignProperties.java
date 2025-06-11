package ir.stts.bajet.cryptography.config;

public interface ISignProperties {

    String getCurve();

    ISignProperties setCurve(String curve);

    String getProvider();

    ISignProperties setProvider(String provider);

    String getKeyAlgorithm();

    ISignProperties setKeyAlgorithm(String keyAlgorithm);

    String getAlgorithm();

    ISignProperties setAlgorithm(String algorithm);

    String getKeyPairFile();

    ISignProperties setKeyPairFile(String keyPairFile);
}