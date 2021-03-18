package data;

import utils.SecurityUtil;

import java.security.PublicKey;

public class UTXO {
    private final String walletAddress;
    private final int amount;
    private final byte[] publicKeyHash;

    public UTXO(String walletAddress, int amount, PublicKey publicKey){
        this.walletAddress = walletAddress;
        this.amount = amount;
        this.publicKeyHash = SecurityUtil.ripemd160Digest(
                SecurityUtil.sha256Digest(publicKey.getEncoded()));
    }


}
