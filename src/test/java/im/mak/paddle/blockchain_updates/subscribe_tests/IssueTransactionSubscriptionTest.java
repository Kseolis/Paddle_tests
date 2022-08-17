package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getFirstTransaction;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.IssueTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getChainId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getSenderPublicKeyFromTransaction;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setTxInfo;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class IssueTransactionSubscriptionTest extends BaseTest {
    private static int assetDecimals;
    private static String address;
    private static String publicKey;
    private static Account account;
    private static String assetName;
    private static String assetDescription;
    private long fee = ONE_WAVES;
    private byte[] compileScript = node().compileScript(SCRIPT_PERMITTING_OPERATIONS).script().bytes();

    private long amountBefore;
    private long amountAfter;

    @BeforeAll
    static void setUp() {
        async(
                () -> assetDecimals = getRandomInt(0, 8),
                () -> assetName = getRandomInt(1, 900000) + "asset",
                () -> assetDescription = assetName + "test",
                () -> {
                    account = new Account(DEFAULT_FAUCET);
                    address = account.address().toString();
                    publicKey = account.publicKey().toString();
                }
        );
    }

    @Test
    @DisplayName("Check subscription on issue smart asset transaction")
    void subscribeTestForIssueSmartAssetTransaction() {
        amountBefore = account.getWavesBalance();
        amountAfter = amountBefore - ONE_WAVES;
        final int assetQuantity = getRandomInt(1000, 999_999_999);
        final boolean reissue = true;

        final IssueTransactionInfo txInfo = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(reissue)
                .script(SCRIPT_PERMITTING_OPERATIONS));
        final IssueTransaction tx = txInfo.tx();
        final String assetId = tx.assetId().toString();

        setTxInfo(txInfo);

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, account, height, height);
        checkIssueTransactionSubscribe(tx, assetId, assetQuantity, reissue);
    }

    @Test
    @DisplayName("Check subscription on issue transaction")
    void subscribeTestForIssueTransaction() {
        amountBefore = account.getWavesBalance();
        amountAfter = amountBefore - ONE_WAVES;
        compileScript = new byte[0];
        final int assetQuantity = getRandomInt(1000, 999_999_999);
        final boolean reissue = false;

        final IssueTransactionInfo txInfo = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(reissue));
        final IssueTransaction tx = txInfo.tx();
        final String assetId = tx.assetId().toString();

        setTxInfo(txInfo);

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, account, height, height);
        checkIssueTransactionSubscribe(tx, assetId, assetQuantity, reissue);
    }

    @Test
    @DisplayName("Check subscription on issue transaction")
    void subscribeTestForIssueNftTransaction() {
        amountBefore = account.getWavesBalance();
        compileScript = new byte[0];
        fee = MIN_FEE;
        amountAfter = DEFAULT_FAUCET - fee;
        assetDecimals = 0;

        final IssueTransactionInfo txInfo = account.issueNft(i -> i.name(assetName).description(assetDescription));
        final IssueTransaction tx = txInfo.tx();
        final String assetId = tx.assetId().toString();

        setTxInfo(txInfo);

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, account, height, height);
        checkIssueTransactionSubscribe(tx, assetId, 1, false);
    }

    private void checkIssueTransactionSubscribe(IssueTransaction tx, String assetId, int quantity, boolean reissue) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getAssetName(0)).isEqualTo(assetName),
                () -> assertThat(getAssetDescription(0)).isEqualTo(assetDescription),
                () -> assertThat(getAssetAmount(0)).isEqualTo(quantity),
                () -> assertThat(getAssetReissuable(0)).isEqualTo(reissue),
                () -> assertThat(getAssetDecimals(0)).isEqualTo(assetDecimals),
                () -> assertThat(getAssetScript(0)).isEqualTo(compileScript),
                () -> assertThat(getFirstTransaction().getVersion()).isEqualTo(IssueTransaction.LATEST_VERSION),
                () -> assertThat(getFirstTransaction().getFee().getAmount()).isEqualTo(fee),
                () -> assertThat(getTransactionId()).isEqualTo(tx.id().toString()),
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                // check waves balance from balances
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(amountAfter),
                // check assetId and balance from balances
                () -> assertThat(getAssetIdAmountAfter(0, 1)).isEqualTo(assetId),
                () -> assertThat(getAmountBefore(0, 1)).isEqualTo(0),
                () -> assertThat(getAmountAfter(0, 1)).isEqualTo(quantity),
                // check from assets
                () -> assertThat(getAssetIdFromAssetAfter(0, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerAfter(0, 0)).isEqualTo(publicKey),
                // check asset info
                () -> assertThat(getNameAfter(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionAfter(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getQuantityAfter(0, 0)).isEqualTo(String.valueOf(quantity)),
                () -> assertThat(getDecimalsAfter(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getReissueAfter(0, 0)).isEqualTo(String.valueOf(reissue)),
                () -> assertThat(getScriptAfter(0, 0)).isEqualTo(compileScript)
        );
    }
}
