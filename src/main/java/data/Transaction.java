package data;

import utils.SecurityUtil;

import java.security.PublicKey;
import java.util.Arrays;

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

    public UTXO[] getInUTXO() {
        return inUTXO;
    }

    public UTXO[] getOutUTXO() {
        return outUTXO;
    }

    public byte[] getSendSign() {
        return sendSign;
    }

    public PublicKey getSendPublicKey() {
        return sendPublicKey;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "\nTransaction{" +
                "\ninUTXO=" + Arrays.toString(inUTXO) +
                ", \noutUTXO=" + Arrays.toString(outUTXO) +
                ", \nsendSign=" + SecurityUtil.bytes2HexString(sendSign) +
                ", \nsendPublicKey=" + SecurityUtil.bytes2HexString(sendPublicKey.getEncoded()) +
                ", \ntimestamp=" + timestamp +
                '}';
    }
}
