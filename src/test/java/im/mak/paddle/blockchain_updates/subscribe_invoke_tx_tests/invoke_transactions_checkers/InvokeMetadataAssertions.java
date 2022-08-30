package im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers;

import java.util.Map;

import static im.mak.paddle.helpers.Convert.convertAddressToHash;
import static im.mak.paddle.blockchain_updates.invoke_subscribe_helpers.PrepareInvokeTestsData.getDAppAccountAddress;
import static im.mak.paddle.blockchain_updates.invoke_subscribe_helpers.PrepareInvokeTestsData.getDAppFunctionName;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeMetadataDAppAddress;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeMetadataFunctionName;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataArgs.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataPayment.getInvokeMetadataPaymentsAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataPayment.getInvokeMetadataPaymentsAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultBurn.getInvokeMetadataResultBurnAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultBurn.getInvokeMetadataResultBurnAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultInvokes.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataLeasesAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataLeasesRecipientPublicKey;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultReissue.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultSponsorFee.getInvokeMetadataResultSponsorFeeAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultSponsorFee.getInvokeMetadataResultSponsorFeeAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultTransfers.*;
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

    public static void checkIssueAssetMetadata(int metadataIndex, int dataIndex, Map<String, String> assetData) {
        assertAll(
                () -> assertThat(getInvokeMetadataResultIssueName(metadataIndex, dataIndex))
                        .isEqualTo(assetData.get(NAME)),
                () -> assertThat(getInvokeMetadataResultIssueDescription(metadataIndex, dataIndex))
                        .isEqualTo(assetData.get(DESCRIPTION)),
                () -> assertThat(getInvokeMetadataResultIssueAmount(metadataIndex, dataIndex))
                        .isEqualTo(Long.valueOf(assetData.get(VOLUME))),
                () -> assertThat(getInvokeMetadataResultIssueDecimals(metadataIndex, dataIndex))
                        .isEqualTo(Long.parseLong(assetData.get(DECIMALS))),
                () -> assertThat(getInvokeMetadataResultIssueReissuable(metadataIndex, dataIndex))
                        .isEqualTo(Boolean.parseBoolean(assetData.get(REISSUE)))
        );
        if (assetData.get(NONCE) != null) {
            assertThat(getInvokeMetadataResultIssueNonce(metadataIndex, dataIndex))
                    .isEqualTo(Long.parseLong(assetData.get(NONCE)));
        }
    }

    public static void checkPaymentMetadata(int metadataIndex, int dataIndex, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getInvokeMetadataPaymentsAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
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
                () -> assertThat(getInvokeMetadataLeasesRecipientPublicKey(metadataIndex, dataIndex))
                        .isEqualTo(publicKeyHash),
                () -> assertThat(getInvokeMetadataLeasesAmount(metadataIndex, dataIndex)).isEqualTo(amount)
        );
    }

    public static void checkTransfersMetadata(int metadataIndex, int dataIndex, byte[] address, String assetId, long amount) {
        byte[] hash = convertAddressToHash(address);
        if (assetId != null) {
            assertThat(getInvokeMetadataResultTransfersAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getInvokeMetadataResultTransfersAddress(metadataIndex, dataIndex)).isEqualTo(hash),
                () -> assertThat(getInvokeMetadataResultTransfersAmount(metadataIndex, dataIndex)).isEqualTo(amount)
        );
    }

    public static void checkResultInvokesMetadata(int metadataIndex, int dataIndex, String dApp, String func) {
        assertAll(
                () -> assertThat(getInvokeMetadataResultInvokesDApp(metadataIndex, dataIndex)).isEqualTo(dApp),
                () -> assertThat(getInvokeMetadataResultInvokesCallFunc(metadataIndex, dataIndex)).isEqualTo(func)
        );
    }

    public static void checkResultInvokesMetadataPayments
            (int metadataIndex, int dataIndex, int payIndex, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getInvokeMetadataResultInvokesPaymentAssetId(metadataIndex, dataIndex, payIndex))
                    .isEqualTo(assetId);
        }
        assertThat(getInvokeMetadataResultInvokesPaymentAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amount);
    }

    public static void checkResultInvokesMetadataStateChanges(int metadataIndex, int dataIndex,
                                                              int payIndex, String assetId,
                                                              byte[] address, long amount) {
        byte[] hash = convertAddressToHash(address);

        if (assetId != null) {
            assertThat(getInvokeMetadataResultInvokesStateChangesTransferAssetId(metadataIndex, dataIndex, payIndex))
                    .isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(
                        getInvokeMetadataResultInvokesStateChangesTransferAddress(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(hash),
                () -> assertThat(
                        getInvokeMetadataResultInvokesStateChangesTransferAmount(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(amount)
        );
    }
}
