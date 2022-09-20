package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcBurnCheckers;
import im.mak.paddle.helpers.transaction_senders.BurnTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.BurnTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class BurnTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static int assetQuantity;
    private static int assetDecimals;
    private static String assetName;
    private static String assetDescription;
    private static Account account;
    private static long assetAmountBefore;
    private static long assetAmountAfter;

    @BeforeAll
    static void setUp() {
        async(
                () -> {
                    assetQuantity = getRandomInt(1000, 999_999_999);
                    assetDecimals = getRandomInt(0, 8);
                    assetName = getRandomInt(1, 900000) + "asset";
                    assetDescription = assetName + "test";
                },
                () -> account = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("Check subscription on burn smart asset transaction")
    void subscribeTestForBurnSmartAssetTransaction() {
        final IssueTransaction issueTx = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)
                .script(SCRIPT_PERMITTING_OPERATIONS)).tx();
        final AssetId assetId = issueTx.assetId();
        final Amount amount = Amount.of(getRandomInt(100, 10000), assetId);

        BurnTransactionSender txSender =
                new BurnTransactionSender(account, amount, SUM_FEE, LATEST_VERSION);
        txSender.burnTransactionSender();
        assetAmountBefore = issueTx.quantity();
        assetAmountAfter = issueTx.quantity() - txSender.getAmount().value();
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcBurnCheckers grpcBurnCheckers = new GrpcBurnCheckers(0, account, txSender, issueTx);
        grpcBurnCheckers.checkBurnSubscribe(assetAmountBefore, assetAmountAfter, assetAmountAfter);
    }

    @Test
    @DisplayName("Check subscription on burn asset transaction")
    void subscribeTestForBurnAssetTransaction() {
        final IssueTransaction issueTx = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)).tx();
        final AssetId assetId = issueTx.assetId();
        final Amount amount = Amount.of(getRandomInt(100, 10000000), assetId);

        BurnTransactionSender txSender =
                new BurnTransactionSender(account, amount, MIN_FEE, LATEST_VERSION);
        txSender.burnTransactionSender();
        assetAmountBefore = issueTx.quantity();
        assetAmountAfter = issueTx.quantity() - txSender.getAmount().value();
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcBurnCheckers grpcBurnCheckers = new GrpcBurnCheckers(0, account, txSender, issueTx);
        grpcBurnCheckers.checkBurnSubscribe(assetAmountBefore, assetAmountAfter, assetAmountAfter);
    }
}
