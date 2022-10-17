package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcTransferCheckers;
import im.mak.paddle.helpers.transaction_senders.TransferTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.TransferTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class TransferTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Account sender;
    private Account recipient;

    private Amount amountTransfer;
    private long amountValue;
    private long amountBefore;
    private long amountAfter;

    @BeforeEach
    void setUp() {
        async(
                () -> sender = new Account(DEFAULT_FAUCET),
                () -> recipient = new Account()
        );
    }

    @Test
    @DisplayName("Check subscription on transfer transaction")
    void subscribeTestForTransferTransaction() {
        amountBefore = sender.getWavesBalance();
        amountTransfer = Amount.of(getRandomInt(1, 10000));
        amountValue = amountTransfer.value();
        amountAfter = amountBefore - MIN_FEE - amountValue;

        TransferTransactionSender txSender = new TransferTransactionSender(amountTransfer, sender, recipient, MIN_FEE);

        txSender.transferTransactionSender(ADDRESS, LATEST_VERSION);
        String txId = txSender.getTransferTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcTransferCheckers grpcTransferCheckers = new GrpcTransferCheckers(0, sender, recipient, txSender);
        grpcTransferCheckers.checkTransferSubscribe(amountBefore, amountAfter, amountBefore, amountAfter);
    }

    @Test
    @DisplayName("Check subscription on transfer transaction issue asset")
    void subscribeTestForTransferTransactionIssueAsset() {
        IssueTransaction issuedAsset = sender.issue(i -> i.name("Test_Asset").quantity(1000)).tx();

        amountBefore = sender.getWavesBalance();
        amountAfter = amountBefore - MIN_FEE;
        amountTransfer = Amount.of(getRandomInt(1, 1000), issuedAsset.assetId());
        amountValue = amountTransfer.value();

        long assetAmount = issuedAsset.quantity() - amountTransfer.value();

        TransferTransactionSender txSender =
                new TransferTransactionSender(amountTransfer, sender, recipient, MIN_FEE);

        txSender.transferTransactionSender(ADDRESS, LATEST_VERSION);
        String txId = txSender.getTransferTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcTransferCheckers grpcTransferCheckers = new GrpcTransferCheckers(0, sender, recipient, txSender);
        grpcTransferCheckers.checkTransferSubscribe(amountBefore, amountAfter, issuedAsset.quantity(), assetAmount);
    }

    @Test
    @DisplayName("Check subscription on transfer transaction issue smart asset")
    void subscribeTestForTransferTransactionIssueSmartAsset() {
        IssueTransaction issuedAsset = sender.issue(i -> i
                .name("Test_Asset")
                .quantity(1000)
                .script(SCRIPT_PERMITTING_OPERATIONS)).tx();

        amountBefore = sender.getWavesBalance();
        amountAfter = amountBefore - SUM_FEE;
        amountTransfer = Amount.of(getRandomInt(1, 1000), issuedAsset.assetId());
        amountValue = amountTransfer.value();

        long assetAmount = issuedAsset.quantity() - amountTransfer.value();

        TransferTransactionSender txSender =
                new TransferTransactionSender(amountTransfer, sender, recipient, SUM_FEE);

        txSender.transferTransactionSender(ADDRESS, LATEST_VERSION);
        String txId = txSender.getTransferTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcTransferCheckers grpcTransferCheckers = new GrpcTransferCheckers(0, sender, recipient, txSender);
        grpcTransferCheckers.checkTransferSubscribe(amountBefore, amountAfter, issuedAsset.quantity(), assetAmount);
    }
}
