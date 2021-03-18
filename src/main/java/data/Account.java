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
        KeyPair keyPair = SecurityUtil.secp256k1Generate();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public String getWalletAddress() {
        //先对公钥进行sha256和ripemd160哈希
        byte[] publicKeyHash = SecurityUtil.ripemd160Digest(
                SecurityUtil.sha256Digest(privateKey.getEncoded()));
        //0x00 + 公钥哈希
        byte [] data = new byte[1 + publicKeyHash.length];
        data[0] = (byte) 0;
        for(int i = 0;i < publicKeyHash.length;i++){
            data[i + 1] = publicKeyHash[i];
        }
        //再对0x00 + 公钥哈希进行double sha256, 取前4字节
        byte [] doublesha = SecurityUtil.sha256Digest(SecurityUtil.sha256Digest(data));
        //构建钱包地址
        byte[] walletEncoded = new byte[data.length + 4];
        for(int i = 0;i < data.length;i++) {
            walletEncoded[i] = data[i];
        }
        for(int i = 0;i < 4;i++) {
            walletEncoded[i + data.length] = doublesha[i];
        }
        //对得到的二进制进行Base58编码
        String walletAddress = Base58Util.encode(walletEncoded);
        return walletAddress;
    }

    @Override
    public String toString() {
        return "Account{" +
                "publicKey=" + SecurityUtil.bytes2HexString(publicKey.getEncoded()) +
                ", privateKey=" + SecurityUtil.bytes2HexString(privateKey.getEncoded()) +
                '}';
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
