package ir.stts.bajet.cryptography.config;

public interface ICryptoProperties {

    ISignProperties getSign();

    ICryptoProperties setSign(ISignProperties ec);

    IKeyExchangeProperties getKeyExchange();

    ICryptoProperties setKeyExchange(IKeyExchangeProperties ec);

    IAesProperties getAes();

    ICryptoProperties setAes(IAesProperties aes);

    IRsaProperties getRsa();

    ICryptoProperties setRsa(IRsaProperties rsa);
}