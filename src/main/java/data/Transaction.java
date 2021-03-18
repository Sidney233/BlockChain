package data;

import java.security.PublicKey;

/**
 * 对交易的抽象
 */
public class Transaction {

    private final UTXO[] inUTXO;
    private final UTXO[] outUTXO;

    private final byte[] sendSign;
    private final PublicKey sendPublicKey;
    private final long timestamp;

    public Transaction(UTXO[] inUTXO, UTXO[] outUTXO, byte[] sendSign, PublicKey sendPublicKey, long timestamp) {
        this.inUTXO = inUTXO;
        this.outUTXO = outUTXO;
        this.sendSign = sendSign;
        this.sendPublicKey = sendPublicKey;
        this.timestamp = timestamp;
    }
}
