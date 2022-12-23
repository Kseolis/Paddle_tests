package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcUpdateAssetInfoCheckers;
import im.mak.paddle.helpers.transaction_senders.UpdateAssetInfoSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.UpdateAssetInfoTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.MIN_FEE;
import static org.testcontainers.utility.Base58.randomString;

public class UpdateAssetInfoTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static Account sender;
    private static PrivateKey senderPrivateKey;
    private static AssetId assetId;
    private static AssetId smartAssetId;
    private static Amount feeAmount;
    private static String assetName;
    private static String assetDescription;
    private static String newAssetName;
    private static String newAssetDescription;
    private static long amountBefore;
    private static long amountAfter;
    private static IssueTransaction issueTransactionInfo;
    private static IssueTransaction issueTransactionSmartAssetInfo;

    @BeforeAll
    static void before() {
        async(
                () -> {
                    sender = new Account(DEFAULT_FAUCET);
                    senderPrivateKey = sender.privateKey();
                    assetName = randomNumAndLetterString(10);
                    assetDescription = randomNumAndLetterString(getRandomInt(2, 50));
                    issueTransactionInfo = sender.issue(i -> i.name(assetName).description(assetDescription)).tx();
                    issueTransactionSmartAssetInfo = sender.issue(i -> i.name(assetName).description(assetDescription).script(SCRIPT_PERMITTING_OPERATIONS)).tx();
                    assetId = issueTransactionInfo.assetId();
                    smartAssetId = issueTransactionSmartAssetInfo.assetId();
                }
        );
    }

    @Test
    @DisplayName("Checks subscription update asset info transaction - minimally short name and description")
    void subscribeUpdateAssetInfoMinimallyShortNameAndDescriptionTest() {
        height = node().getHeight();
        feeAmount = Amount.of(MIN_FEE);
        amountBefore = sender.getWavesBalance();
        amountAfter = amountBefore - MIN_FEE;
        newAssetName = randomString(4);
        newAssetDescription = "";
        UpdateAssetInfoSender txSender = new UpdateAssetInfoSender(assetId, newAssetName, newAssetDescription, feeAmount, senderPrivateKey);
        txSender.updateAssetInfoSending(LATEST_VERSION, 0);
        String txId = txSender.getUpdAssetInfoTxId().toString();
        subscribeResponseHandler(CHANNEL, height, node().getHeight(), txId);
        GrpcUpdateAssetInfoCheckers updateAssetInfoCheckers = new GrpcUpdateAssetInfoCheckers(txSender, issueTransactionInfo);
        updateAssetInfoCheckers.checkUpdateAssetInfo(getTxIndex(), amountBefore, amountAfter);
    }

    @Test
    @DisplayName("Checks subscription update asset info transaction - longest possible name and description for smart asset")
    void subscribeUpdateAssetInfoForSmartAssetLongestPossibleNameAndDescriptionTest() {
        height = node().getHeight();
        feeAmount = Amount.of(MIN_FEE);
        amountBefore = sender.getWavesBalance();
        amountAfter = amountBefore - SUM_FEE;
        newAssetName = randomString(16);
        newAssetDescription = randomString(1000);
        UpdateAssetInfoSender txSender = new UpdateAssetInfoSender(smartAssetId, newAssetName, newAssetDescription, feeAmount, senderPrivateKey);
        txSender.updateAssetInfoSending(LATEST_VERSION, EXTRA_FEE);
        String txId = txSender.getUpdAssetInfoTxId().toString();
        subscribeResponseHandler(CHANNEL, height, node().getHeight(), txId);
        GrpcUpdateAssetInfoCheckers updateAssetInfoCheckers = new GrpcUpdateAssetInfoCheckers(txSender, issueTransactionSmartAssetInfo);
        updateAssetInfoCheckers.checkUpdateAssetInfo(getTxIndex(), amountBefore, amountAfter);
    }
}
