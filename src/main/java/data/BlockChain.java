package data;

import config.MiniChainConfig;
import utils.SecurityUtil;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

/**
 * 区块链的类抽象，创建该对象时会自动生成创世纪块，加入区块链中
 */
public class BlockChain {

    private final LinkedList<Block> chain = new LinkedList<>();
    private final Account[] accounts;

    public BlockChain() {
        this.accounts=new Account[MiniChainConfig.ACCOUNT_NUM];
        for(int i=0;i<accounts.length;++i){
            accounts[i]=new Account();
        }

        //为accounts中的成员初始化余额
        Transaction[] transactions=genesisTransactions(accounts);

        BlockHeader genesisBlockHeader = new BlockHeader(null, null,
                                                            Math.abs(new Random().nextLong()));
        BlockBody genesisBlockBody=new BlockBody(null,transactions);
        Block genesisBlock = new Block(genesisBlockHeader,genesisBlockBody);
        System.out.println("Create the genesis Block! ");
        System.out.println("And the hash of genesis Block is : " + SecurityUtil.sha256Digest(genesisBlock.toString()) +
                ", you will see the hash value in next Block's preBlockHash field.");
        System.out.println();
        chain.add(genesisBlock);
    }


    private Transaction[] genesisTransactions(Account[] accounts){
        //为accounts中的每个成员都支付一笔金额
        UTXO[] outUtxos =new UTXO[accounts.length];
        for(int i=0;i<accounts.length;++i){
            outUtxos[i]=new UTXO(accounts[i].getWalletAddress(),MiniChainConfig.INIT_ACCOUNT,accounts[i].getPublicKey());
        }

        //为刚开始的交易随机生成公钥，和签名等信息
        KeyPair dayDreamKeyPair=SecurityUtil.secp256k1Generate();
        PublicKey dayDreamPublicKey=dayDreamKeyPair.getPublic();
        PrivateKey dayDreamPrivateKey=dayDreamKeyPair.getPrivate();
        byte[] sign=SecurityUtil.signature("Everything in the dream!".getBytes(StandardCharsets.UTF_8),dayDreamPrivateKey);
        return new Transaction[]{new Transaction(new UTXO[]{},outUtxos,sign,dayDreamPublicKey,System.currentTimeMillis())};

    }

    //查询用户可用的utxo
    public UTXO[] getTrueUtxos(String walletAddress){

        Set<UTXO> trueUtxoSet=new HashSet<>();//相当于python中的set，可以去重

        //遍历区块链中所有的区块，然后遍历所有的交易，获取utxo
        for(Block block:chain){
            BlockBody blockBody =block.getBlockBody();
            Transaction[] transactions=blockBody.getTransactions();

            for(Transaction transaction :transactions){
                UTXO[] inUtxos= transaction.getInUtxos();
                UTXO[] outUtxos=transaction.getOutUtxos();

                //统计出所有转给自己的utxo存入trueUtxoSet
                for(UTXO utxo:outUtxos){
                    if(utxo.getWalletAddress().equals(walletAddress)){
                        trueUtxoSet.add(utxo);
                    }
                }
                //去除用户自己花费的utxo
                for(UTXO utxo:inUtxos){
                    if(utxo.getWalletAddress().equals(walletAddress)){
                        trueUtxoSet.remove(utxo);
                    }
                }


            }
        }

        UTXO[] trueUtxos=new UTXO[trueUtxoSet.size()];
        trueUtxoSet.toArray(trueUtxos);
        return trueUtxos;
    }

    //用来验证余额总量是不是不变
    public int getAllAccountAmount(){
        int sumAmount=0;
        for(int i=0;i<accounts.length;++i){
            UTXO[] trueUtxo=getTrueUtxos((accounts[i].getWalletAddress()));
            sumAmount += accounts[i].getAmount(trueUtxo);

        }
        return  sumAmount;
    }
    /**
     * 向区块链中添加新的满足难度条件的区块
     *
     * @param block 新的满足难度条件的区块
     */
    public void addNewBlock(Block block) {
        chain.offer(block);
    }

    /**
     * 获取区块链的最后一个区块，矿工在组装新的区块时，需要获取上一个区块的哈希值，通过该方法获得
     *
     * @return 区块链的最后一个区块
     */
    public Block getNewestBlock() {
        return chain.peekLast();
    }

    public Account[] getAccounts() {
        return accounts;
    }
}
