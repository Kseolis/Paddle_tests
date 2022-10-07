package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcSetAssetScriptCheckers;
import im.mak.paddle.helpers.transaction_senders.SetAssetScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SetAssetScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.ScriptUtil.fromFile;

public class SetAssetScriptTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private int assetQuantity;
    private int assetDecimals;
    private Account account;
    private String assetName;
    private String assetDescription;
    private final byte[] firstScript = node().compileScript(SCRIPT_PERMITTING_OPERATIONS).script().bytes();
    private final Base64String newScript = node()
            .compileScript(fromFile("ride_scripts/permissionOnUpdatingKeyValues.ride")).script();

    @BeforeEach
    void setUp() {
        async(
                () -> assetQuantity = getRandomInt(1000, 999_999_999),
                () -> assetDecimals = getRandomInt(0, 8),
                () -> assetQuantity = getRandomInt(1000, 999_999_999),
                () -> {
                    assetName = getRandomInt(1, 900000) + "asset";
                    assetDescription = assetName + "test";
                },
                () -> account = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("Check subscription on setAssetScript smart asset transaction")
    void subscribeTestForSetAssetScriptTransaction() {
        final IssueTransactionInfo issue = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true).script(SCRIPT_PERMITTING_OPERATIONS));
        final IssueTransaction issueTx = issue.tx();
        final AssetId assetId = issueTx.assetId();

        SetAssetScriptTransactionSender senderTx = new SetAssetScriptTransactionSender(account, newScript, assetId);
        senderTx.setAssetScriptTransactionSender(LATEST_VERSION);
        String txId = senderTx.getSetAssetScriptTx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcSetAssetScriptCheckers grpcSetAssetScriptCheckers = new GrpcSetAssetScriptCheckers(0, senderTx, issueTx);
        grpcSetAssetScriptCheckers.checkSetAssetGrpc(firstScript);
    }
}
