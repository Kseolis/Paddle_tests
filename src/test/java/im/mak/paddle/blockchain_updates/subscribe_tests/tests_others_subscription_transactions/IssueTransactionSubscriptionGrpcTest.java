package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcIssueCheckers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.*;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setTxInfo;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;

public class IssueTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static int assetDecimals;
    private static String address;
    private static String publicKey;
    private static Account account;
    private static String assetName;
    private static String assetDescription;

    private IssueTransactionInfo txInfo;
    private IssueTransaction tx;
    private String txId;

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

        txInfo = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(reissue)
                .script(SCRIPT_PERMITTING_OPERATIONS));
        tx = txInfo.tx();
        txId = tx.id().toString();

        setTxInfo(txInfo);

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcIssueCheckers getIssueCheckers = new GrpcIssueCheckers(0, address, publicKey, tx);
        getIssueCheckers.checkIssueGrpc(amountBefore, amountAfter);
    }

    @Test
    @DisplayName("Check subscription on issue transaction")
    void subscribeTestForIssueTransaction() {
        amountBefore = account.getWavesBalance();
        amountAfter = amountBefore - ONE_WAVES;
        final int assetQuantity = getRandomInt(1000, 999_999_999);
        final boolean reissue = false;

        txInfo = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(reissue));
        tx = txInfo.tx();
        txId = tx.id().toString();

        setTxInfo(txInfo);

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcIssueCheckers getIssueCheckers = new GrpcIssueCheckers(0, address, publicKey, tx);
        getIssueCheckers.checkIssueGrpc(amountBefore, amountAfter);
    }

    @Test
    @DisplayName("Check subscription on issue transaction")
    void subscribeTestForIssueNftTransaction() {
        amountBefore = account.getWavesBalance();
        amountAfter = DEFAULT_FAUCET - MIN_FEE;
        assetDecimals = 0;

        txInfo = account.issueNft(i -> i.name(assetName).description(assetDescription));
        tx = txInfo.tx();
        txId = tx.id().toString();

        setTxInfo(txInfo);

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcIssueCheckers getIssueCheckers = new GrpcIssueCheckers(0, address, publicKey, tx);
        getIssueCheckers.checkIssueGrpc(amountBefore, amountAfter);
    }
}
