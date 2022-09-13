package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.helpers.transaction_senders.ReissueTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.ReissueTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.ReissueTransactionHandler.getReissueAssetAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.ReissueTransactionHandler.getReissueAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ReissueTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Amount amount;
    private long quantityAfterReissue;
    private long wavesAmountAfterReissue;
    private int assetQuantity;
    private int assetDecimals;
    private String address;
    private String publicKey;
    private Account account;
    private String assetName;
    private String assetDescription;
    private byte[] compileScript = node().compileScript(SCRIPT_PERMITTING_OPERATIONS).script().bytes();
    private final long wavesAmountBeforeReissue = DEFAULT_FAUCET - ONE_WAVES;

    @BeforeEach
    void setUp() {
        async(
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
    @DisplayName("Check subscription on reissue asset transaction")
    void subscribeTestForReissueAsset() {
        wavesAmountAfterReissue = wavesAmountBeforeReissue - SUM_FEE;
        compileScript = new byte[0];

        IssueTransaction issueTx = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)).tx();

        AssetId assetId = issueTx.assetId();
        amount = Amount.of(getRandomInt(100, 10000000), assetId);
        quantityAfterReissue = assetQuantity + amount.value();

        ReissueTransactionSender txSender = new ReissueTransactionSender(account, amount, assetId);
        txSender.reissueTransactionSender(SUM_FEE, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, account, height, height, txId);
        checkReissueSubscribe(txSender);
    }

    @Test
    @DisplayName("Check subscription on reissue smart asset transaction")
    void subscribeTestForReissueSmartAsset() {
        wavesAmountAfterReissue = wavesAmountBeforeReissue - SUM_FEE;

        IssueTransaction issueTx = account.issue(i -> i
                .name(assetName)
                .quantity(assetQuantity)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)
                .script(SCRIPT_PERMITTING_OPERATIONS)).tx();

        AssetId assetId = issueTx.assetId();
        amount = Amount.of(getRandomInt(100, 10000000), assetId);
        quantityAfterReissue = assetQuantity + amount.value();

        ReissueTransactionSender txSender = new ReissueTransactionSender(account, amount, assetId);
        txSender.reissueTransactionSender(SUM_FEE, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, account, height, height, txId);
        checkReissueSubscribe(txSender);
    }

    private void checkReissueSubscribe(ReissueTransactionSender txSender) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(SUM_FEE),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getReissueAssetAmount(0)).isEqualTo(txSender.getAmount().value()),
                () -> assertThat(getReissueAssetId(0)).isEqualTo(txSender.getAssetId().toString()),
                () -> assertThat(getTransactionId()).isEqualTo(txSender.getReissueTx().id().toString()),
                // check waves balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(wavesAmountBeforeReissue),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(wavesAmountAfterReissue),
                // check asset balance
                () -> assertThat(getAddress(0, 1)).isEqualTo(address),
                () -> assertThat(getAssetIdAmountAfter(0, 1)).isEqualTo(txSender.getAssetId().toString()),
                () -> assertThat(getAmountBefore(0, 1)).isEqualTo(assetQuantity),
                () -> assertThat(getAmountAfter(0, 1)).isEqualTo(quantityAfterReissue),
                // check asset before reissue
                () -> assertThat(getAssetIdFromAssetBefore(0, 0)).isEqualTo(txSender.getAssetId().toString()),
                () -> assertThat(getIssuerBefore(0, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityBefore(0, 0)).isEqualTo(String.valueOf(assetQuantity)),
                () -> assertThat(getReissueBefore(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getNameBefore(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionBefore(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getDecimalsBefore(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getScriptBefore(0, 0)).isEqualTo(compileScript),
                // check asset after reissue
                () -> assertThat(getAssetIdFromAssetAfter(0, 0)).isEqualTo(txSender.getAssetId().toString()),
                () -> assertThat(getIssuerAfter(0, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityAfter(0, 0)).isEqualTo(String.valueOf(quantityAfterReissue)),
                () -> assertThat(getReissueAfter(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getNameAfter(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionAfter(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getDecimalsAfter(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getScriptAfter(0, 0)).isEqualTo(compileScript)
        );
    }
}
