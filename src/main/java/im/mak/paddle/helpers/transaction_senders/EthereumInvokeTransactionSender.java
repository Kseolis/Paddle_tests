package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.helpers.CalculateBalancesAfterEthTransactions;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.util.List;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static com.wavesplatform.transactions.EthereumTransaction.DEFAULT_GAS_PRICE;

public class EthereumInvokeTransactionSender extends BaseTransactionSender {
    private final Address senderAddress;
    private final Address recipientAddress;
    private final List<Amount> payments;
    private EthereumTransaction ethTx;
    private Id ethTxId;
    private long timestamp;
    private final long ethInvokeFee;
    private final CalculateBalancesAfterEthTransactions balances = new CalculateBalancesAfterEthTransactions();

    public EthereumInvokeTransactionSender(Address senderAddress, Address recipientAddress, List<Amount> payments, long ethInvokeFee) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.payments = payments;
        this.ethInvokeFee = ethInvokeFee;
    }

    public void sendingAnEthereumInvokeTransaction(Function function, List<Amount> amounts) throws NodeException, IOException {
        byte chainId = node().chainId();
        ECKeyPair keyPair = getEthInstance().getEcKeyPair();
        timestamp = System.currentTimeMillis();

        ethTx = EthereumTransaction.invocation(recipientAddress, function, payments, DEFAULT_GAS_PRICE, chainId, ethInvokeFee, timestamp, keyPair);
        ethTxId = ethTx.id();

        balances.calculateBalancesForAmounts(senderAddress, recipientAddress, amounts, ethInvokeFee);

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

    public CalculateBalancesAfterEthTransactions getBalances() {
        return balances;
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
