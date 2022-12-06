package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.exceptions.NodeException;

import java.io.IOException;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;

public class EthereumTransactionSender extends BaseTransactionSender {
    private final Address senderAddress;
    private final Address recipientAddress;
    private final Amount amountTransfer;
    private EthereumTransaction ethTx;
    private Id ethTxId;

    private long senderBalanceBeforeTransaction;
    private long senderBalanceAfterTransaction;
    private long recipientBalanceBeforeTransaction;
    private long recipientBalanceAfterTransaction;

    public EthereumTransactionSender(Address senderAddress, Address recipientAddress, Amount amountTransfer) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.amountTransfer = amountTransfer;
    }

    public void sendingAnEthereumTransaction(long fee) throws NodeException, IOException {
        senderBalanceBeforeTransaction = node().getBalance(senderAddress);
        senderBalanceAfterTransaction = senderBalanceBeforeTransaction - amountTransfer.value() - fee;
        recipientBalanceBeforeTransaction = node().getBalance(recipientAddress);
        recipientBalanceAfterTransaction = recipientBalanceBeforeTransaction + amountTransfer.value();

        ethTx = EthereumTransaction.transfer(
                recipientAddress,
                amountTransfer,
                EthereumTransaction.DEFAULT_GAS_PRICE,
                node().chainId(),
                fee,
                System.currentTimeMillis(),
                getEthInstance().getEcKeyPair()
        );
        ethTxId = ethTx.id();

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

    public long getSenderBalanceBeforeTransaction() {
        return senderBalanceBeforeTransaction;
    }

    public long getSenderBalanceAfterTransaction() {
        return senderBalanceAfterTransaction;
    }

    public long getRecipientBalanceBeforeTransaction() {
        return recipientBalanceBeforeTransaction;
    }

    public long getRecipientBalanceAfterTransaction() {
        return recipientBalanceAfterTransaction;
    }

    public Amount getAmountTransfer() {
        return amountTransfer;
    }

    public EthereumTransaction getEthTx() {
        return ethTx;
    }

    public Id getEthTxId() {
        return ethTxId;
    }
}
