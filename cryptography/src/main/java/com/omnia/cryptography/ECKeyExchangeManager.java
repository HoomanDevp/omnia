package com.omnia.cryptography;

import com.omnia.cryptography.config.IKeyExchangeProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

@RequiredArgsConstructor
public class ECKeyExchangeManager {

    private KeyPair keyPair;

    private final IKeyExchangeProperties properties;
    private final ECKeyExchangeKeyManager ecKeyExchangeKeyManager;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    void init() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        this.keyPair = ecKeyExchangeKeyManager.generateEcKeyPair();
    }

    public byte[] generateServerPublicKey() {

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        return extractEcKeyBytesFromDerKey(publicKeyBytes);
    }

    public PublicKey derivePublicKeyFromBytes(byte[] ecKeyBytes) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        String curve = properties.getCurve();
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(curve);
        ECNamedCurveSpec params = new ECNamedCurveSpec(
                curve,
                spec.getCurve(),
                spec.getG(),
                spec.getN()
        );

        ECPoint publicPoint = ECPointUtil.decodePoint(params.getCurve(), ecKeyBytes);
        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(publicPoint, params);

        String provider = properties.getProvider();
        String algorithm = properties.getAlgorithm();
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm, provider);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public byte[] generateSecretKey(PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {

        String provider = properties.getProvider();
        String algorithm = properties.getAlgorithm();
        KeyAgreement keyAgreement = KeyAgreement.getInstance(algorithm, provider);
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(publicKey, true);

        return keyAgreement.generateSecret();
    }

    public boolean checkSecretKey(byte[] clientSharedSecret, byte[] serverSharedSecret) {
        return MessageDigest.isEqual(serverSharedSecret, clientSharedSecret);
    }

    private byte[] extractEcKeyBytesFromDerKey(byte[] derEncodedKey) {

        ASN1Sequence sequence = DERSequence.getInstance(derEncodedKey);
        DERBitString subjectPublicKey = (DERBitString) sequence.getObjectAt(1);
        return subjectPublicKey.getBytes();
    }
}