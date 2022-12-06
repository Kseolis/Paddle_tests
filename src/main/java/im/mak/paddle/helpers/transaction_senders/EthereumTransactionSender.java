package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

import static im.mak.paddle.Node.node;

public class EthereumTransactionSender extends BaseTransactionSender {
    private final Address senderAddress;
    private final Address recipientAddress;
    private final Amount amountTransfer;
    private final ECKeyPair ecKeyPair;
    private EthereumTransaction ethTx;
    private Id ethTxId;

    private long senderBalanceBeforeTransaction;
    private long senderBalanceAfterTransaction;

    public EthereumTransactionSender(Address senderAddress, Address recipientAddress, Amount amountTransfer, ECKeyPair ecKeyPair) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.amountTransfer = amountTransfer;
        this.ecKeyPair = ecKeyPair;
    }

    public void sendingAnEthereumTransaction(long fee) throws NodeException, IOException {
        senderBalanceBeforeTransaction = node().getBalance(senderAddress);
        senderBalanceAfterTransaction = senderBalanceBeforeTransaction - amountTransfer.value() - fee;
        ethTx = EthereumTransaction.transfer(
                recipientAddress,
                amountTransfer,
                EthereumTransaction.DEFAULT_GAS_PRICE,
                node().chainId(),
                fee,
                System.currentTimeMillis(),
                ecKeyPair
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

    public Amount getAmountTransfer() {
        return amountTransfer;
    }

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public EthereumTransaction getEthTx() {
        return ethTx;
    }

    public Id getEthTxId() {
        return ethTxId;
    }
}
