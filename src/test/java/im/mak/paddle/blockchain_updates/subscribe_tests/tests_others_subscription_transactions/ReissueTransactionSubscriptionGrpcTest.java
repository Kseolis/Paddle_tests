package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcReissueCheckers;
import im.mak.paddle.helpers.transaction_senders.ReissueTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.ReissueTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class ReissueTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Amount amount;
    private long quantityAfterReissue;
    private int assetQuantity;
    private int assetDecimals;
    private Account sender;
    private Account senderSmart;
    private String assetName;
    private String assetDescription;

    @BeforeEach
    void setUp() {
        async(
                () -> assetDecimals = getRandomInt(0, 8),
                () -> assetQuantity = getRandomInt(1000, 999_999_999),
                () -> {
                    assetName = getRandomInt(1, 900000) + "asset";
                    assetDescription = assetName + "test";
                },
                () -> sender = new Account(DEFAULT_FAUCET),
                () -> senderSmart = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("Check subscription on reissue asset transaction")
    void subscribeTestForReissueAsset() {
        IssueTransaction issueTx = sender.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)).tx();

        AssetId assetId = issueTx.assetId();
        amount = Amount.of(getRandomInt(100, 10000000), assetId);
        quantityAfterReissue = assetQuantity + amount.value();

        ReissueTransactionSender txSender = new ReissueTransactionSender(sender, amount, assetId);
        txSender.reissueTransactionSender(SUM_FEE, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcReissueCheckers grpcReissueCheckers = new GrpcReissueCheckers(0, sender, txSender, issueTx);
        grpcReissueCheckers.checkReissueSubscribe(
                assetQuantity,
                quantityAfterReissue,
                assetQuantity,
                quantityAfterReissue
        );
    }

    @Test
    @DisplayName("Check subscription on reissue smart asset transaction")
    void subscribeTestForReissueSmartAsset() {
        IssueTransaction issueTx = senderSmart.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)
                .script(SCRIPT_PERMITTING_OPERATIONS)).tx();

        AssetId assetId = issueTx.assetId();
        amount = Amount.of(getRandomInt(100, 10000000), assetId);
        quantityAfterReissue = assetQuantity + amount.value();

        ReissueTransactionSender txSender = new ReissueTransactionSender(senderSmart, amount, assetId);
        txSender.reissueTransactionSender(SUM_FEE, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcReissueCheckers grpcReissueCheckers = new GrpcReissueCheckers(getTxIndex(), senderSmart, txSender, issueTx);
        grpcReissueCheckers.checkReissueSubscribe(
                assetQuantity,
                quantityAfterReissue,
                assetQuantity,
                quantityAfterReissue
        );
    }
}
