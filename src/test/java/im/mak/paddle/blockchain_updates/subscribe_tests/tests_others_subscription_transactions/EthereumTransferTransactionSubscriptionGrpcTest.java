package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcEthereumTransferCheckers;
import im.mak.paddle.helpers.transaction_senders.EthereumTransactionSender;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Convert.wavesAddressFromETH;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class EthereumTransferTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Address senderAddress;
    private final String senderEthPrivateKey = "0f1477865b6b251e1d400b0efc4b4d9e2fdd7c32fa8d3de3bcac3c3abfe7c07c";
    private final String ethAddressWithoutTheFirstBytes = "2A0eaaDD531CcC84Fc56Fb44645274A96583DFA7";
    private ECKeyPair ecKeyPair;
    private Account recipient;
    private Address recipientAddress;
    private Amount amountTransfer;

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    try {
                        senderAddress = Address.as(wavesAddressFromETH(ethAddressWithoutTheFirstBytes));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                    ecKeyPair = ECKeyPair.create(Hex.decode(senderEthPrivateKey));
                },
                () -> {
                    recipient = new Account();
                    recipientAddress = recipient.address();
                },
                () -> amountTransfer = Amount.of(1)
        );
    }

    @Test
    @DisplayName("Check subscription on Ethereum transfer transaction")
    void subscribeTestForTransferTransaction() throws NodeException, IOException {
        EthereumTransactionSender txSender = new EthereumTransactionSender(senderAddress, recipientAddress, amountTransfer, ecKeyPair);
        txSender.sendingAnEthereumTransaction(MIN_FEE);
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txSender.getEthTxId().toString());
        GrpcEthereumTransferCheckers checkers = new GrpcEthereumTransferCheckers(getTxIndex(), txSender, amountTransfer.value());
        checkers.checkEthereumTransfer();
    }
}
