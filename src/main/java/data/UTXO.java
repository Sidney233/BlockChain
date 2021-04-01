package data;

import utils.SecurityUtil;

import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Stack;

public class UTXO {
    //一个UTXO需要获得放的钱包，交易数额，和交易获得放的公钥
    private final String walletAddress;
    private final int amount;
    private  final byte[] publickKeyHash;
    //一个UTXO需要获得放的钱包，交易数额，和交易获得放的公钥
    public UTXO(String walletAddress, int amount, PublicKey publicKey){
        this.walletAddress=walletAddress;
        this.amount=amount;
        //UTXO存入获得方的公钥哈希，以供获得方比对
        publickKeyHash= SecurityUtil.ripemd160Digest(SecurityUtil.sha256Digest(publicKey.getEncoded()));
    }
    public boolean unlockScript(byte[] sign,PublicKey publicKey){

        Stack<byte[]> stack=new Stack<>();
        //签名入栈
        stack.push(sign);
        //公钥入栈
        //<sign><pubkey>
        stack.push(publicKey.getEncoded());
        //<sign><pubkey><pubkey>
        stack.push(stack.peek());
        //顶端公钥取出哈希一次再入栈
        //<sign><pubkey><pubkeyhash1>
        byte[] data=stack.pop();
        stack.push(SecurityUtil.ripemd160Digest(SecurityUtil.sha256Digest(data)));
        //UTXO中存储的公钥哈希入栈
        //<sign><pubkey><pubkeyhash1><pubkeyhash2>
        stack.push(publickKeyHash);
        //比对栈顶两个元素，如果等就表明是可以解锁的公钥，全部取出
        byte[] publicKeyHash1=stack.pop();
        byte[] publicKeyHash2=stack.pop();
        if(!Arrays.equals(publicKeyHash1,publicKeyHash2)){
            return false;
        }
        //<sign><pubkey>
        byte[] publicKeyEncoded=stack.pop();
        byte[] sign1=stack.pop();

        return  SecurityUtil.verify(publicKey.getEncoded(),sign1,publicKey);
    }
    public String getWalletAddress(){return walletAddress;}

    public int getAmount() {
        return amount;
    }

    public byte[] getPublickKeyHash() {
        return publickKeyHash;
    }

    @Override
    public String toString() {
        return "\n\tUTXO{" +
                "walletAddress='" + walletAddress + '\'' +
                ", amount=" + amount +
                ", publickKeyHash=" + SecurityUtil.bytes2HexString(publickKeyHash) +
                '}';
    }
}
