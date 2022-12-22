package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static com.wavesplatform.transactions.EthereumTransaction.DEFAULT_GAS_PRICE;

public class EthereumTransferTransactionSender extends BaseTransactionSender {
    private final Address senderAddress;
    private final Address recipientAddress;
    private final Amount amountTransfer;
    private EthereumTransaction ethTx;
    private Id ethTxId;
    private long timestamp;
    private final long fee;
    private long senderWavesBalanceBeforeTransaction;
    private long senderWavesBalanceAfterTransaction;
    private long recipientWavesBalanceBeforeTransaction;
    private long recipientWavesBalanceAfterTransaction;
    private long senderAssetBalanceBeforeTransaction;
    private long senderAssetBalanceAfterTransaction;
    private long recipientAssetBalanceBeforeTransaction;
    private long recipientAssetBalanceAfterTransaction;

    public EthereumTransferTransactionSender(Address senderAddress, Address recipientAddress, Amount amountTransfer, long fee) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.amountTransfer = amountTransfer;
        this.fee = fee;
    }

    public void sendingAnEthereumTransferTransaction() throws NodeException, IOException {
        byte chainId = node().chainId();
        ECKeyPair keyPair = getEthInstance().getEcKeyPair();
        timestamp = System.currentTimeMillis();

        ethTx = EthereumTransaction.transfer(recipientAddress, amountTransfer, DEFAULT_GAS_PRICE, chainId, fee, timestamp, keyPair);
        ethTxId = ethTx.id();

        calculateBalancesForAmount(senderAddress, recipientAddress, amountTransfer, fee);
        node().broadcastEthTransaction(ethTx);
        node().waitForTransaction(ethTxId);

        txInfo = node().getTransactionInfo(ethTxId);
    }

    public Address getRecipientAddress() {
        return recipientAddress;
    }

    public Address getSenderAddress() {
        return senderAddress;
    }

    public long getEthTimestamp() {
        return timestamp;
    }

    public EthereumTransaction getEthTx() {
        return ethTx;
    }

    public long getEthFee() {
        return fee;
    }

    public Id getEthTxId() {
        return ethTxId;
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

    private void calculateBalancesForAmount(Address senderAddress, Address recipientAddress, Amount amountTransfer, long fee) {
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
}
