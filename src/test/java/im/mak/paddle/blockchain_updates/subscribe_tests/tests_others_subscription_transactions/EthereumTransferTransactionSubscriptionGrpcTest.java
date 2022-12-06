package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcEthereumTransferCheckers;
import im.mak.paddle.helpers.EthereumTestUser;
import im.mak.paddle.helpers.transaction_senders.EthereumTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class EthereumTransferTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private Account recipient;
    private Address recipientAddress;
    private Amount amountTransfer;

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    try {
                        ethInstance = getEthInstance();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethInstance.getSenderAddress();
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    recipient = new Account();
                    recipientAddress = recipient.address();
                },
                () -> amountTransfer = Amount.of(getRandomInt(100, 100_000))
        );
    }

    @Test
    @DisplayName("Check subscription on Ethereum transfer transaction")
    void subscribeTestForTransferTransaction() throws NodeException, IOException {
        EthereumTransactionSender txSender = new EthereumTransactionSender(senderAddress, recipientAddress, amountTransfer);
        txSender.sendingAnEthereumTransaction(MIN_FEE);
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txSender.getEthTxId().toString());
        GrpcEthereumTransferCheckers checkers = new GrpcEthereumTransferCheckers(getTxIndex(), txSender, amountTransfer.value());
        checkers.checkEthereumTransfer();
    }
}
