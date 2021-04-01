package data;

import utils.SecurityUtil;

import java.security.PublicKey;
import java.util.Arrays;

/**
 * 对交易的抽象
 */
public class Transaction {
    private final UTXO[] inUtxos;
    private final UTXO[] outUtxos;
    //发送方的签名，发送方的公钥，方便验证
    private final byte[] sendSign;
    private final PublicKey sendPublickey;
    private final long timestamp;

    public Transaction(UTXO[] inUtxos,UTXO[] outUtxos,byte[] sendSign,PublicKey sendPublickey,long timestamp) {
        this.outUtxos=outUtxos;
        this.inUtxos=inUtxos;
        this.sendSign=sendSign;
        this.sendPublickey=sendPublickey;
        this.timestamp = timestamp;
    }

    public UTXO[] getOutUtxos() {
        return outUtxos;
    }

    public UTXO[] getInUtxos() {
        return inUtxos;
    }

    public byte[] getSendsign() {
        return sendSign;
    }

    public PublicKey getSendPublickey() {
        return sendPublickey;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "\nTransaction{" +
                "\ninUtxos=" + Arrays.toString(inUtxos) +
                ", \noutUtxos=" + Arrays.toString(outUtxos) +
                ", \nsendsign=" + SecurityUtil.bytes2HexString(sendSign) +
                ", \nsendPublickey=" + SecurityUtil.bytes2HexString(sendPublickey.getEncoded()) +
                ", \ntimestamp=" + timestamp +
                '}';
    }
}
