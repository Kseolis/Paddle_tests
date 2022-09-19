package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.SponsorFeeTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SponsorFeeTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SponsorFeeTransactionHandler.getAmountFromSponsorFee;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SponsorFeeTransactionHandler.getAssetIdFromSponsorFee;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SponsorFeeTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static int assetQuantity;
    private static int assetDecimals;

    private static Account account;

    private static AssetId assetId;
    private static String assetName;
    private static String assetDescription;

    private static DefaultDApp420Complexity dAppAccount;


    private static long sponsorFeeAmount;
    private long wavesAmountBefore;
    private long wavesAmountAfter;

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
        wavesAmountBefore = account.getWavesBalance() - ONE_WAVES;
        wavesAmountAfter = wavesAmountBefore - SUM_FEE;

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
        checkSponsorFeeSubscribe(txSender, SUM_FEE);
    }

    @Test
    @DisplayName("Check subscription on sponsorFee DAppAccount asset transaction")
    void subscribeTestForDAppAccountSponsorFeeTransaction() {
        assetName = getRandomInt(1, 900000) + "asset DApp";
        assetDescription = assetName + "test DApp";
        assetQuantity = getRandomInt(1000, 999_999_999);
        assetDecimals = getRandomInt(0, 8);
        wavesAmountBefore = dAppAccount.getWavesBalance() - ONE_WAVES - EXTRA_FEE;
        wavesAmountAfter = wavesAmountBefore - SUM_FEE;
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
        checkSponsorFeeSubscribe(txSender, SUM_FEE);
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
        wavesAmountBefore = account.getWavesBalance() - ONE_WAVES;
        wavesAmountAfter = wavesAmountBefore - MIN_FEE;
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

        checkSponsorFeeSubscribe(txSender, MIN_FEE);
    }

    private void checkSponsorFeeSubscribe(SponsorFeeTransactionSender txSender, long fee) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0))
                        .isEqualTo(txSender.getAssetOwner().publicKey().toString()),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getAssetIdFromSponsorFee(0)).isEqualTo(assetId.toString()),
                () -> assertThat(getAmountFromSponsorFee(0)).isEqualTo(sponsorFeeAmount),
                () -> assertThat(getTransactionId()).isEqualTo(txSender.getSponsorTx().id().toString()),
                // check waves balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(txSender.getAssetOwner().address().toString()),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(wavesAmountBefore),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(wavesAmountAfter),
                // check asset before sponsor fee transaction
                () -> assertThat(getAssetIdFromAssetBefore(0, 0)).isEqualTo(assetId.toString()),
                () -> assertThat(getIssuerBefore(0, 0)).isEqualTo(txSender.getAssetOwner().publicKey().toString()),
                () -> assertThat(getDecimalsBefore(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getNameBefore(0, 0)).isEqualTo(String.valueOf(assetName)),
                () -> assertThat(getDescriptionBefore(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getReissueBefore(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getQuantityBefore(0, 0)).isEqualTo(String.valueOf(assetQuantity)),
                () -> assertThat(getScriptComplexityBefore(0, 0)).isEqualTo(0),
                // check asset after sponsor fee transaction
                () -> assertThat(getAssetIdFromAssetAfter(0, 0)).isEqualTo(assetId.toString()),
                () -> assertThat(getIssuerAfter(0, 0)).isEqualTo(txSender.getAssetOwner().publicKey().toString()),
                () -> assertThat(getDecimalsAfter(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getNameAfter(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionAfter(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getReissueAfter(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getQuantityAfter(0, 0)).isEqualTo(String.valueOf(assetQuantity)),
                () -> assertThat(getScriptComplexityAfter(0, 0)).isEqualTo(0)
        );
    }
}
