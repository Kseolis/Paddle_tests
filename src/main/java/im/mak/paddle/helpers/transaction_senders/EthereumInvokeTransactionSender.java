package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.helpers.EthereumTestUser;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.util.List;

import static im.mak.paddle.Node.node;
import static com.wavesplatform.transactions.EthereumTransaction.DEFAULT_GAS_PRICE;

public class EthereumInvokeTransactionSender extends BaseTransactionSender {
    private final Address recipientAddress;
    private final List<Amount> payments;
    private final EthereumTestUser sender;
    private EthereumTransaction ethTx;
    private Id ethTxId;
    private long timestamp;
    private final long ethInvokeFee;

    public EthereumInvokeTransactionSender(Address recipientAddress, List<Amount> payments, long ethInvokeFee, EthereumTestUser sender) throws IOException {
        this.recipientAddress = recipientAddress;
        this.payments = payments;
        this.ethInvokeFee = ethInvokeFee;
        this.sender = sender;
    }

    public void sendingAnEthereumInvokeTransaction(Function function) throws NodeException, IOException {
        byte chainId = node().chainId();
        ECKeyPair keyPair = sender.getEcKeyPair();
        timestamp = System.currentTimeMillis();

        ethTx = EthereumTransaction.invocation(recipientAddress, function, payments, DEFAULT_GAS_PRICE, chainId, ethInvokeFee, timestamp, keyPair);
        ethTxId = ethTx.id();

        node().broadcastEthTransaction(ethTx);
        node().waitForTransaction(ethTxId);

        txInfo = node().getTransactionInfo(ethTxId);
    }

    public Address getRecipientAddress() {
        return recipientAddress;
    }

    public List<Amount> getPayments() {
        return payments;
    }

    public EthereumTransaction getEthTx() {
        return ethTx;
    }

    public Id getEthTxId() {
        return ethTxId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getEthInvokeFee() {
        return ethInvokeFee;
    }
}
