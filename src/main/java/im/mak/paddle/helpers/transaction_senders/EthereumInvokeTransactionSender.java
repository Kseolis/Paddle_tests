package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.util.List;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static com.wavesplatform.transactions.EthereumTransaction.DEFAULT_GAS_PRICE;

public class EthereumInvokeTransactionSender extends BaseTransactionSender {
    private final Address senderAddress;
    private final Address recipientAddress;
    private final Amount amountTransfer;
    private EthereumTransaction ethTx;
    private Id ethTxId;
    private long timestamp;
    private final long fee;

    public EthereumInvokeTransactionSender(Address senderAddress, Address recipientAddress, Amount amountTransfer, long fee) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.amountTransfer = amountTransfer;
        this.fee = fee;
    }

    public void sendingAnEthereumInvokeTransaction(Function function, List<Amount> payments) throws NodeException, IOException {
        byte chainId = node().chainId();
        ECKeyPair keyPair = getEthInstance().getEcKeyPair();
        timestamp = System.currentTimeMillis();

        ethTx = EthereumTransaction.invocation(recipientAddress, function, payments, DEFAULT_GAS_PRICE, chainId, fee, timestamp, keyPair);

        ethTxId = ethTx.id();

        node().broadcastEthTransaction(ethTx);
        node().waitForTransaction(ethTxId);

        txInfo = node().getTransactionInfo(ethTxId);
    }
}
