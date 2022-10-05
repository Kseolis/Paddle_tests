package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.LeaseCancelTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.LeaseCancelTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class LeaseCancelTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Account sender;

    private Account recipient;

    private int amountLease;
    private static final DefaultDApp420Complexity accWithDApp = new DefaultDApp420Complexity(DEFAULT_FAUCET);

    @BeforeEach
    void setUp() {
        async(
                () -> amountLease = getRandomInt(100_000, 1_00_000_000),
                () -> sender = new Account(DEFAULT_FAUCET),
                () -> recipient = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("Check subscription on lease waves transaction")
    void subscribeTestForWavesLeaseTransaction() {
        final LeaseTransaction leaseTx = sender.lease(recipient, amountLease).tx();
        final Id leaseId = leaseTx.id();

        LeaseCancelTransactionSender txSender = new LeaseCancelTransactionSender(sender, recipient, MIN_FEE);

        txSender.leaseCancelTransactionSender(leaseId, amountLease, LATEST_VERSION);
        final String leaseCancelId = txSender.getLeaseCancelTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, leaseCancelId);

      //  checkLeaseCancelSubscribe(leaseIdString, leaseCancelId, MIN_FEE);
    }

    @Test
    @DisplayName("Check subscription on lease waves transaction in smartAcc")
    void subscribeTestForWavesLeaseTransactionDAppAcc() {
        final LeaseTransaction leaseTx = accWithDApp.lease(recipient, amountLease).tx();
        final Id leaseId = leaseTx.id();

        LeaseCancelTransactionSender txSender = new LeaseCancelTransactionSender(accWithDApp, recipient, SUM_FEE);
        txSender.leaseCancelTransactionSender(leaseId, amountLease, LATEST_VERSION);
        final String leaseCancelId = txSender.getLeaseCancelTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, leaseCancelId);

      //  checkLeaseCancelSubscribe(leaseCancelId, SUM_FEE);
    }
}
