package consensus;

import data.*;
import utils.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 生成随机交易
 */
public class TransactionProducer extends Thread {

    private final TransactionPool transactionPool;
    private final BlockChain blockChain;

    public TransactionProducer(TransactionPool transactionPool, BlockChain blockChain) {
        this.transactionPool = transactionPool;
        this.blockChain = blockChain;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (transactionPool) {
                while (transactionPool.isFull()) {
                    try {
                        transactionPool.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Transaction randomOne = getOneTransaction();
                transactionPool.put(randomOne);
                if (transactionPool.isFull()) {
                    transactionPool.notify();
                }
            }
        }
    }

    private Transaction getOneTransaction() {
        Random random = new Random();
        Transaction transaction = null;
        Account[] accounts = blockChain.getAccounts();

        while (true) {
            Account aAccount = accounts[random.nextInt(accounts.length)];
            Account bAccount = accounts[random.nextInt(accounts.length)];
            if (aAccount == bAccount) {
                continue;
            }
            String aWalletAddress = aAccount.getWalletAddress();
            String bWalletAddress = bAccount.getWalletAddress();
            UTXO[] aTrueUtxos = blockChain.getTrueUtxos(aWalletAddress);
            int aAmount = aAccount.getAmount(aTrueUtxos);
            if (aAmount == 0) {
                continue;
            }
            int txAmount = random.nextInt(aAmount) + 1;
            List<UTXO> inUtxoList = new ArrayList<>();
            List<UTXO> outUtxolList = new ArrayList<>();
            byte[] aUnlockSign = SecurityUtil.signature(aAccount.getPublicKey().getEncoded(), aAccount.getPrivateKey());
            int inAmount = 0;
            for (UTXO utxo : aTrueUtxos) {
                if (utxo.unlockScript(aUnlockSign, aAccount.getPublicKey())) {
                    inAmount += utxo.getAmount();
                    inUtxoList.add(utxo);
                    if (inAmount >= txAmount) {
                        break;
                    }
                }
            }
            if (inAmount < txAmount) {
                continue;
            }
            outUtxolList.add(new UTXO(bWalletAddress, txAmount, bAccount.getPublicKey()));
            if (inAmount > txAmount) {
                outUtxolList.add(new UTXO(aWalletAddress, inAmount-txAmount, aAccount.getPublicKey()));
            }
            UTXO[] inUtxos = inUtxoList.toArray(new UTXO[0]);
            UTXO[] outUtxos = outUtxolList.toArray(new UTXO[0]);
            byte[] data = SecurityUtil.utxos2Bytes(inUtxos, outUtxos);
            byte[] sign = SecurityUtil.signature(data, aAccount.getPrivateKey());
            long timestamp = System.currentTimeMillis();
            transaction = new Transaction(inUtxos, outUtxos, sign, aAccount.getPublicKey(), timestamp);
            break;
        }
        return transaction;
    }

}
