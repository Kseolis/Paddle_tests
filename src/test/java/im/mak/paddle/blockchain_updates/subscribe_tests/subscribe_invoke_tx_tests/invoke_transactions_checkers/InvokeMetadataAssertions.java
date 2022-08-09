package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers;

import org.bouncycastle.util.encoders.Base64;

import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeBaseTest.getDAppAccountAddress;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeBaseTest.getDAppFunctionName;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetNonce;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeMetadataDAppAddress;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeMetadataFunctionName;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataArgs.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataPayment.getInvokeMetadataPaymentsAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultBurn.getInvokeMetadataResultBurnAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultBurn.getInvokeMetadataResultBurnAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.getInvokeMetadataResultIssueNonce;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataLeasesAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataLeasesRecipientPublicKey;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultReissue.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultSponsorFee.getInvokeMetadataResultSponsorFeeAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultSponsorFee.getInvokeMetadataResultSponsorFeeAssetId;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeMetadataAssertions {
    public static void checkMainMetadata(int index) {
        assertAll(
                () -> assertThat(getInvokeMetadataDAppAddress(index)).isEqualTo(getDAppAccountAddress()),
                () -> assertThat(getInvokeMetadataFunctionName(index)).isEqualTo(getDAppFunctionName())
        );
    }

    public static void checkArgumentsMetadata(int metadataIndex, int dataIndex, String argType, String argValue) {
        switch (argType) {
            case BINARY_BASE58:
                assertThat(getInvokeMetadataArgBinaryValueBase58(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BINARY_BASE64:
                assertThat(getInvokeMetadataArgBinaryValueBase64(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case INTEGER:
                assertThat(getInvokeMetadataArgIntegerValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case STRING:
                assertThat(getInvokeMetadataArgStringValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BOOLEAN:
                assertThat(getInvokeMetadataArgBooleanValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            default:
                break;
        }
    }

    public static void checkIssueAssetMetadata(int metadataIndex, int dataIndex) {
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

    public static void checkPaymentMetadata(int metadataIndex, int dataIndex, long amount) {
        assertThat(getInvokeMetadataPaymentsAmount(metadataIndex, dataIndex)).isEqualTo(amount);
    }

    public static void checkBurnMetadata(int metadataIndex, int dataIndex, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getInvokeMetadataResultBurnAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertThat(getInvokeMetadataResultBurnAmount(metadataIndex, dataIndex)).isEqualTo(amount);
    }

    public static void checkSponsorFeeMetadata(int metadataIndex, int dataIndex, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getInvokeMetadataResultSponsorFeeAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertThat(getInvokeMetadataResultSponsorFeeAmount(metadataIndex, dataIndex)).isEqualTo(amount);
    }

    public static void checkReissueMetadata
            (int metadataIndex, int dataIndex, String assetId, long amount, boolean reissue) {
        if (assetId != null) {
            assertThat(getInvokeMetadataResultReissueAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getInvokeMetadataResultReissueAmount(metadataIndex, dataIndex)).isEqualTo(amount),
                () -> assertThat(getInvokeMetadataResultReissueIsReissuable(metadataIndex, dataIndex)).isEqualTo(reissue)
        );
    }

    public static void checkDataMetadata(int metadataIndex, int dataIndex, String type, String key, String argValue) {
        switch (type) {
            case BINARY_BASE58:
                assertThat(getInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getInvokeMetadataResultDataBinaryBase58Value(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BINARY_BASE64:
                assertThat(getInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getInvokeMetadataResultDataBinaryBase64Value(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case INTEGER:
                assertThat(getInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getInvokeMetadataResultDataIntegerValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case STRING:
                assertThat(getInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getInvokeMetadataResultDataStringValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BOOLEAN:
                assertThat(getInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getInvokeMetadataResultDataBoolValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            default:
                break;
        }
    }

    public static void checkLeaseMetadata(int metadataIndex, int dataIndex, String publicKeyHash, long amount) {
        assertAll(
                () -> assertThat(getInvokeMetadataLeasesRecipientPublicKey(metadataIndex, dataIndex)).isEqualTo(publicKeyHash),
                () -> assertThat(getInvokeMetadataLeasesAmount(metadataIndex, dataIndex)).isEqualTo(amount)
        );
    }
}
