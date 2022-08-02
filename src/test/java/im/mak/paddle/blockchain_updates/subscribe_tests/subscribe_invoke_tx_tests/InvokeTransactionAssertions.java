package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetAmount;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getCallerAccount;
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
import static im.mak.paddle.util.Constants.STAGENET_CHAIN_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.bouncycastle.cert.jcajce.JcaX500NameUtil.getIssuer;

public class InvokeTransactionAssertions extends InvokeBaseTest {

    protected static void checkInvokeSubscribe(long amount, long fee) {
        assertThat(getChainId(0)).isEqualTo(STAGENET_CHAIN_ID);
        assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(getCallerAccount().publicKey().toString());
        assertThat(getTransactionFeeAmount(0)).isEqualTo(fee);
        assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION);
        assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(dAppAccountPublicKeyHash);
        //  assertThat(getInvokeTransactionFunctionCall(0)).isEqualTo(getDAppCall().getFunction().toString());
        assertThat(getTransactionId()).isEqualTo(getInvokeScriptId());
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
        assertThat(getInvokeMetadataResultIssueName(metadataIndex, dataIndex)).isEqualTo(getIssuedAssetName());
        assertThat(getInvokeMetadataResultIssueDescription(metadataIndex, dataIndex)).isEqualTo(getIssuedAssetDescription());
        assertThat(getInvokeMetadataResultIssueAmount(metadataIndex, dataIndex)).isEqualTo(getQuantity());
        assertThat(getInvokeMetadataResultIssueDecimals(metadataIndex, dataIndex)).isEqualTo(getDecimals());
        assertThat(getInvokeMetadataResultIssueReissuable(metadataIndex, dataIndex)).isEqualTo(true);
        assertThat(getInvokeMetadataResultIssueNonce(metadataIndex, dataIndex)).isEqualTo(getNonce());
    }

    protected static void checkBurnMetadata(int metadataIndex, int dataIndex, String assetId, long amount) {
        if (!assetId.equals("")) {
            assertThat(getInvokeMetadataResultBurnAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertThat(getInvokeMetadataResultBurnAmount(metadataIndex, dataIndex)).isEqualTo(amount);
    }

    protected static void checkStateUpdateBalance(int balanceIndex, String address, String assetId, long amountBefore, long amountAfter) {
        assertThat(getAddress(0, balanceIndex)).isEqualTo(address);
        if (assetId != null) {
            assertThat(getAssetIdAmountAfter(0, balanceIndex)).isEqualTo(assetId);
        }
        assertThat(getAmountBefore(0, balanceIndex)).isEqualTo(amountBefore);
        assertThat(getAmountAfter(0, balanceIndex)).isEqualTo(amountAfter);
    }

    protected static void checkStateUpdateAssets(int txStateUpdIndex, int assetIndex, String assetId) {
        if (!getAssetIdFromAssetBefore(txStateUpdIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetId);
            }
        }

        if (!getAssetIdFromAssetAfter(txStateUpdIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetId);
            }
            assertThat(getIssuerAfter(txStateUpdIndex, assetIndex)).isEqualTo(null);
            assertThat(getDecimalsAfter(txStateUpdIndex, assetIndex)).isEqualTo(getDecimals());
        }
    }
}
