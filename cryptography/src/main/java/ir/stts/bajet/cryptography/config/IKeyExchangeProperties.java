package ir.stts.bajet.cryptography.config;

public interface IKeyExchangeProperties {

    String getCurve();

    IKeyExchangeProperties setCurve(String curve);

    String getProvider();

    IKeyExchangeProperties setProvider(String provider);

    String getKeyAlgorithm();

    IKeyExchangeProperties setKeyAlgorithm(String keyAlgorithm);

    String getAlgorithm();

    IKeyExchangeProperties setAlgorithm(String algorithm);
}