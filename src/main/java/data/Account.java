package data;

import utils.Base58Util;
import utils.SecurityUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Account {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public Account() {
        KeyPair keyPair= SecurityUtil.secp256k1Generate();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }
    public String getWalletAddress(){
        //公钥哈希
        byte[] publicKeyHash=SecurityUtil.ripemd160Digest(SecurityUtil.sha256Digest(publicKey.getEncoded()));
        //往前拼接0x00,
        byte[] data =new byte[1+publicKeyHash.length];
        data[0]=(byte)0;
        for(int i=0;i<publicKeyHash.length;++i){
            data[i+1]=publicKeyHash[i];
        }

        //两次哈希
        byte[] doubleHash=SecurityUtil.sha256Digest(SecurityUtil.sha256Digest(data));

        //前面拼接0x00,后面拼接两次hash之后的前四字节
        byte[] walletEncoded=new byte[1+publicKeyHash.length+4];
        walletEncoded[0]=(byte)0;
        for (int i=0;i<publicKeyHash.length;++i){
            walletEncoded[1+i]=publicKeyHash[i];

        }
        for(int i=0;i<4;++i){
            walletEncoded[1+publicKeyHash.length+i]=doubleHash[i];
        }

        //对钱包进行编码
        String walletAddress= Base58Util.encode(walletEncoded);

        return walletAddress;
    }
    //计算用户余额
    public int getAmount(UTXO[] trueUtxos){
        int amount=0;
        for(int i=0;i<trueUtxos.length;++i){
            amount+=trueUtxos[i].getAmount();
        }
        return amount;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public String toString() {
        return "Account{" +
                "publicKey=" + SecurityUtil.bytes2HexString(publicKey.getEncoded()) +
                ", privateKey=" + SecurityUtil.bytes2HexString(publicKey.getEncoded()) +
                '}';
    }
}
