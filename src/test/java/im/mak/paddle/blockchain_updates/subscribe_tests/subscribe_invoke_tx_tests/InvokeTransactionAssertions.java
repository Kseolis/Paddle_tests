package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import java.util.Map;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeMetadataDAppAddress;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeMetadataFunctionName;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultBurn.getInvokeMetadataResultBurnAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultBurn.getInvokeMetadataResultBurnAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.getInvokeMetadataResultIssueNonce;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.getInvokeTransactionPaymentAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.getInvokeTransactionPublicKeyHash;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeTransactionAssertions extends InvokeBaseTest {

    protected static void checkInvokeSubscribe(long amount, long fee) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(STAGENET_CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(getCallerAccount().publicKey().toString()),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(dAppAccountPublicKeyHash),
                //  () -> assertThat(getInvokeTransactionFunctionCall(0)).isEqualTo(getDAppCall().getFunction().toString());
                () -> assertThat(getTransactionId()).isEqualTo(getInvokeScriptId())
        );
    }

    protected static void checkPaymentsSubscribe(long amount) {
        assertThat(getInvokeTransactionPaymentAmount(0, 0)).isEqualTo(getAssetAmount().value());
        assertThat(getInvokeTransactionPaymentAmount(0, 1)).isEqualTo(amount);
    }

    protected static void checkMainMetadata(int index) {
        assertThat(getInvokeMetadataDAppAddress(index)).isEqualTo(dAppAccountAddress);
        assertThat(getInvokeMetadataFunctionName(index)).isEqualTo(dAppFunctionName);
    }

    protected static void checkIssueAssetMetadata(int metadataIndex, int dataIndex) {
        assertAll(
                () -> assertThat(getInvokeMetadataResultIssueName(metadataIndex, dataIndex))
                        .isEqualTo(getIssuedAssetName()),
                () -> assertThat(getInvokeMetadataResultIssueDescription(metadataIndex, dataIndex))
                        .isEqualTo(getIssuedAssetDescription()),
                () -> assertThat(getInvokeMetadataResultIssueAmount(metadataIndex, dataIndex))
                        .isEqualTo(getIssueAssetVolume()),
                () -> assertThat(getInvokeMetadataResultIssueDecimals(metadataIndex, dataIndex))
                        .isEqualTo(getIssueAssetDecimals()),
                () -> assertThat(getInvokeMetadataResultIssueReissuable(metadataIndex, dataIndex))
                        .isEqualTo(getIssueAssetReissuable()),
                () -> assertThat(getInvokeMetadataResultIssueNonce(metadataIndex, dataIndex))
                        .isEqualTo(getIssueAssetNonce())
        );
    }

    protected static void checkBurnMetadata(int metadataIndex, int dataIndex, String assetId, long amount) {
        if (!assetId.equals("")) {
            assertThat(getInvokeMetadataResultBurnAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertThat(getInvokeMetadataResultBurnAmount(metadataIndex, dataIndex)).isEqualTo(amount);
    }

    protected static void checkStateUpdateBalance
            (int balanceIndex, String address, String assetId, long amountBefore, long amountAfter) {
        if (assetId != null) {
            assertThat(getAssetIdAmountAfter(0, balanceIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getAddress(0, balanceIndex)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, balanceIndex)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(0, balanceIndex)).isEqualTo(amountAfter)
        );
    }

    protected static void checkStateUpdateAssets(int txStateUpdIndex, int assetIndex, Map<String, String> assetData) {
        String assetId = assetData.get(ASSET_ID);
        String quantityAfter = String.valueOf(Integer.parseInt(assetData.get(VOLUME)) - getAssetAmount().value());
        if (!getAssetIdFromAssetBefore(txStateUpdIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetId);
            }
            assertThat(getIssuerBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(ISSUER));
            assertThat(getDecimalsBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DECIMALS));
            assertThat(getNameBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(NAME));
            assertThat(getDescriptionBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DESCRIPTION));
            assertThat(getQuantityBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(VOLUME));
        }

        if (!getAssetIdFromAssetAfter(txStateUpdIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetId);
            }
            assertThat(getIssuerAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(ISSUER));
            assertThat(getDecimalsAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DECIMALS));
            assertThat(getNameAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(NAME));
            assertThat(getDescriptionAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DESCRIPTION));
            assertThat(getQuantityAfter(txStateUpdIndex, assetIndex)).isEqualTo(quantityAfter);
        }
    }
}
