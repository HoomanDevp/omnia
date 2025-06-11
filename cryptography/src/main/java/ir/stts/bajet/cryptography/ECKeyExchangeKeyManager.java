package ir.stts.bajet.cryptography;

import ir.stts.bajet.cryptography.config.IKeyExchangeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.security.*;

@RequiredArgsConstructor
public class ECKeyExchangeKeyManager {

    private final IKeyExchangeProperties properties;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyPair generateEcKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(properties.getKeyAlgorithm(), properties.getProvider());
        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(properties.getCurve());
        keyGen.initialize(parameterSpec);

        return keyGen.generateKeyPair();
    }
}