package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.SetAssetScriptTransaction;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SetAssetScriptTransactionHandler.getAssetIdFromSetAssetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SetAssetScriptTransactionHandler.getScriptFromSetAssetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setTxInfo;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.ScriptUtil.fromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SetAssetScriptTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private int assetQuantity;
    private int assetDecimals;
    private String address;
    private String publicKey;
    private Account account;
    private String assetName;
    private String assetDescription;
    private final long wavesAmountBeforeSetAssetScript = DEFAULT_FAUCET - ONE_WAVES;
    private final long wavesAmountAfterSetAssetScript = wavesAmountBeforeSetAssetScript - ONE_WAVES;
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
                () -> {
                    account = new Account(DEFAULT_FAUCET);
                    address = account.address().toString();
                    publicKey = account.publicKey().toString();
                }
        );
    }

    @Test
    @DisplayName("Check subscription on setAssetScript smart asset transaction")
    void subscribeTestForSetAssetScriptTransaction() {
        final IssueTransactionInfo txInfo = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true).script(SCRIPT_PERMITTING_OPERATIONS));
        final IssueTransaction tx = txInfo.tx();
        final AssetId assetId = tx.assetId();
        final String assetIdToString = assetId.toString();

        setTxInfo(txInfo);

        SetAssetScriptTransaction setAssetScriptTx = account.setAssetScript(assetId, newScript).tx();
        String txId = setAssetScriptTx.id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        checkSetAssetSubscribe(setAssetScriptTx, assetIdToString);

    }

    private void checkSetAssetSubscribe(SetAssetScriptTransaction setAssetScriptTx, String assetId) {

        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(ONE_WAVES),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(SetAssetScriptTransaction.LATEST_VERSION),
                () -> assertThat(getAssetIdFromSetAssetScript(0)).isEqualTo(assetId),
                () -> assertThat(getScriptFromSetAssetScript(0)).isEqualTo(newScript.bytes()),
                () -> assertThat(getTransactionId()).isEqualTo(setAssetScriptTx.id().toString()),
                // check waves balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(wavesAmountBeforeSetAssetScript),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(wavesAmountAfterSetAssetScript),
                // check asset before set asset script
                () -> assertThat(getAssetIdFromAssetBefore(0, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerBefore(0, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityBefore(0, 0)).isEqualTo(String.valueOf(assetQuantity)),
                () -> assertThat(getReissueBefore(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getNameBefore(0, 0)).isEqualTo(String.valueOf(assetName)),
                () -> assertThat(getDescriptionBefore(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getDecimalsBefore(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getScriptBefore(0, 0)).isEqualTo(firstScript),
                () -> assertThat(getScriptComplexityBefore(0, 0)).isEqualTo(0),
                // check asset after set asset script
                () -> assertThat(getAssetIdFromAssetAfter(0, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerAfter(0, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityAfter(0, 0)).isEqualTo(String.valueOf(assetQuantity)),
                () -> assertThat(getReissueAfter(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getNameAfter(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionAfter(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getDecimalsAfter(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getScriptAfter(0, 0)).isEqualTo(newScript.bytes()),
                () -> assertThat(getScriptComplexityAfter(0, 0)).isEqualTo(15)
        );
    }
}
