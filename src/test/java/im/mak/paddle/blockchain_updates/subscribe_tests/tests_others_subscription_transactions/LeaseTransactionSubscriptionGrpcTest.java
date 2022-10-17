package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcLeaseCheckers;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.LeaseTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.LeaseTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;

public class LeaseTransactionSubscriptionGrpcTest extends BaseGrpcTest {
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
    @DisplayName("Check subscription on lease min sum waves transaction")
    void subscribeTestForWavesLeaseTransaction() {
        LeaseTransactionSender txSender = new LeaseTransactionSender(sender, recipient, MIN_FEE);

        txSender.leaseTransactionSender(MIN_TRANSACTION_SUM, LATEST_VERSION);

        String leaseId = txSender.getLeaseTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, leaseId);

        GrpcLeaseCheckers grpcLeaseCheckers = new GrpcLeaseCheckers(0, sender, recipient, txSender);
        grpcLeaseCheckers.checkLeaseGrpc(MIN_FEE, txSender.getAmountBefore(), txSender.getAmountAfter());
    }

    @Test
    @DisplayName("Check subscription on lease transaction in smartAcc")
    void subscribeTestForWavesLeaseTransactionDAppAcc() {
        LeaseTransactionSender txSender = new LeaseTransactionSender(accWithDApp, recipient, SUM_FEE);

        txSender.leaseTransactionSender(amountLease, LATEST_VERSION);

        String leaseId = txSender.getLeaseTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, leaseId);

        GrpcLeaseCheckers grpcLeaseCheckers = new GrpcLeaseCheckers(0, accWithDApp, recipient, txSender);
        grpcLeaseCheckers.checkLeaseGrpc(SUM_FEE, txSender.getAmountBefore(), txSender.getAmountAfter());
    }
}
