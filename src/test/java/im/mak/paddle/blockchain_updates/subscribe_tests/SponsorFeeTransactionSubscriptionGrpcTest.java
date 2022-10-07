package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcSponsorFeeCheckers;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.SponsorFeeTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SponsorFeeTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class SponsorFeeTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static int assetQuantity;
    private static int assetDecimals;

    private static Account account;

    private static AssetId assetId;
    private static String assetName;
    private static String assetDescription;

    private static DefaultDApp420Complexity dAppAccount;

    private static long sponsorFeeAmount;

    @BeforeAll
    static void setUp() {
        async(
                () -> sponsorFeeAmount = getRandomInt(100, 100000),
                () -> account = new Account(DEFAULT_FAUCET),
                () -> dAppAccount = new DefaultDApp420Complexity(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("Check subscription on sponsorFee asset transaction")
    void subscribeTestForSponsorFeeTransaction() {
        assetName = getRandomInt(1, 900000) + "asset";
        assetDescription = assetName + "test";
        assetQuantity = getRandomInt(1000, 999_999_999);
        assetDecimals = getRandomInt(0, 8);

        IssueTransaction issueTx = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)).tx();
        assetId = issueTx.assetId();

        SponsorFeeTransactionSender txSender = new SponsorFeeTransactionSender(account, sponsorFeeAmount, assetId);
        txSender.sponsorFeeTransactionSender(SUM_FEE, LATEST_VERSION);
        String txId = txSender.getSponsorTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcSponsorFeeCheckers grpcSponsorFeeCheckers = new GrpcSponsorFeeCheckers(0, txSender, issueTx);
        grpcSponsorFeeCheckers.checkSponsorFeeGrpc();
    }

    @Test
    @DisplayName("Check subscription on sponsorFee DAppAccount asset transaction")
    void subscribeTestForDAppAccountSponsorFeeTransaction() {
        assetName = getRandomInt(1, 900000) + "asset DApp";
        assetDescription = assetName + "test DApp";
        assetQuantity = getRandomInt(1000, 999_999_999);
        assetDecimals = getRandomInt(0, 8);
        IssueTransaction issueTx = dAppAccount.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)).tx();
        assetId = issueTx.assetId();

        SponsorFeeTransactionSender txSender = new SponsorFeeTransactionSender(dAppAccount, sponsorFeeAmount, assetId);
        txSender.sponsorFeeTransactionSender(SUM_FEE, LATEST_VERSION);
        String txId = txSender.getSponsorTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcSponsorFeeCheckers grpcSponsorFeeCheckers = new GrpcSponsorFeeCheckers(0, txSender, issueTx);
        grpcSponsorFeeCheckers.checkSponsorFeeGrpc();
    }

    @Test
    @DisplayName("Check subscription on cancel sponsorFee asset transaction")
    void subscribeTestForCancelSponsorFeeTransaction() {
        long extraFee = 0;
        assetName = getRandomInt(1, 900000) + "asset";
        assetDescription = assetName + "test";
        assetQuantity = getRandomInt(1000, 999_999_999);
        assetDecimals = getRandomInt(0, 8);
        sponsorFeeAmount = 0;

        IssueTransaction issueTx = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)).tx();
        assetId = issueTx.assetId();

        SponsorFeeTransactionSender txSender = new SponsorFeeTransactionSender(account, sponsorFeeAmount, assetId);
        txSender.cancelSponsorFeeSender(account, account, dAppAccount, LATEST_VERSION, extraFee);
        String txId = txSender.getSponsorTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcSponsorFeeCheckers grpcSponsorFeeCheckers = new GrpcSponsorFeeCheckers(0, txSender, issueTx);
        grpcSponsorFeeCheckers.checkSponsorFeeGrpc();
    }
}
