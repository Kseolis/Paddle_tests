package im.mak.paddle.helpers;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;

import java.util.List;

import static im.mak.paddle.Node.node;

public class CalculateBalancesAfterEthTransactions {
    private long senderWavesBalanceBeforeTransaction;
    private long senderWavesBalanceAfterTransaction;
    private long recipientWavesBalanceBeforeTransaction;
    private long recipientWavesBalanceAfterTransaction;
    private long senderAssetBalanceBeforeTransaction;
    private long senderAssetBalanceAfterTransaction;
    private long recipientAssetBalanceBeforeTransaction;
    private long recipientAssetBalanceAfterTransaction;

    public void calculateBalancesForAmount(Address senderAddress, Address recipientAddress, Amount amountTransfer, long fee) {
        AssetId asset = amountTransfer.assetId().isWaves() ? AssetId.as("") : amountTransfer.assetId();

        senderWavesBalanceBeforeTransaction = node().getBalance(senderAddress);
        senderWavesBalanceAfterTransaction = senderWavesBalanceBeforeTransaction - fee;
        recipientWavesBalanceBeforeTransaction = node().getBalance(recipientAddress);
        recipientWavesBalanceAfterTransaction = recipientWavesBalanceBeforeTransaction;

        if (asset.isWaves()) {
            senderWavesBalanceAfterTransaction -= amountTransfer.value();
            recipientWavesBalanceAfterTransaction += amountTransfer.value();
        } else {
            senderAssetBalanceBeforeTransaction = node().getAssetBalance(senderAddress, amountTransfer.assetId());
            senderAssetBalanceAfterTransaction = senderAssetBalanceBeforeTransaction - amountTransfer.value();

            recipientAssetBalanceBeforeTransaction = node().getAssetBalance(recipientAddress, amountTransfer.assetId());
            recipientAssetBalanceAfterTransaction = recipientAssetBalanceBeforeTransaction + amountTransfer.value();
        }
    }

    public void calculateBalancesForAmounts(Address senderAddress, Address recipientAddress, List<Amount> amounts, long fee) {
        senderWavesBalanceBeforeTransaction = node().getBalance(senderAddress);
        senderWavesBalanceAfterTransaction = senderWavesBalanceBeforeTransaction - fee;
        recipientWavesBalanceBeforeTransaction = node().getBalance(recipientAddress);
        recipientWavesBalanceAfterTransaction = recipientWavesBalanceBeforeTransaction;

        for (Amount amountTransfer : amounts) {
            AssetId asset = amountTransfer.assetId().isWaves() ? AssetId.as("") : amountTransfer.assetId();
            if (asset.isWaves()) {
                senderWavesBalanceAfterTransaction -= amountTransfer.value();
                recipientWavesBalanceAfterTransaction += amountTransfer.value();
            } else {
                senderAssetBalanceBeforeTransaction = node().getAssetBalance(senderAddress, amountTransfer.assetId());
                senderAssetBalanceAfterTransaction = senderAssetBalanceBeforeTransaction - amountTransfer.value();
                recipientAssetBalanceBeforeTransaction = node().getAssetBalance(recipientAddress, amountTransfer.assetId());
                recipientAssetBalanceAfterTransaction = recipientAssetBalanceBeforeTransaction + amountTransfer.value();
            }
        }
    }

    public long getSenderBalanceBeforeEthTransaction() {
        return senderWavesBalanceBeforeTransaction;
    }

    public long getSenderBalanceAfterEthTransaction() {
        return senderWavesBalanceAfterTransaction;
    }

    public long getRecipientBalanceBeforeEthTransaction() {
        return recipientWavesBalanceBeforeTransaction;
    }

    public long getRecipientBalanceAfterEthTransaction() {
        return recipientWavesBalanceAfterTransaction;
    }

    public long getSenderAssetBalanceBeforeTransaction() {
        return senderAssetBalanceBeforeTransaction;
    }

    public long getSenderAssetBalanceAfterTransaction() {
        return senderAssetBalanceAfterTransaction;
    }

    public long getRecipientAssetBalanceBeforeTransaction() {
        return recipientAssetBalanceBeforeTransaction;
    }

    public long getRecipientAssetBalanceAfterTransaction() {
        return recipientAssetBalanceAfterTransaction;
    }

}
