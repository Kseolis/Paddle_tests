package im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.helpers.PrepareInvokeTestsData;

import java.util.Map;

import static im.mak.paddle.blockchain_updates.BaseGrpcTest.getDAppAccountAddress;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.getDAppFunctionName;
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

    public static void checkTransfersMetadata(int metadataIndex, int dataIndex, String address, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getInvokeMetadataResultTransfersAssetId(metadataIndex, dataIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getInvokeMetadataResultTransfersAddress(metadataIndex, dataIndex)).isEqualTo(address),
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

    public static void checkStateChangesTransfers
            (int metadataIndex, int dataIndex, int payIndex, String assetId, long amountValue, String address) {
        if (assetId != null) {
            assertThat(getStateChangesTransferAssetId(metadataIndex, dataIndex, payIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getStateChangesTransferAddress(metadataIndex, dataIndex, payIndex)).isEqualTo(address),
                () -> assertThat(getStateChangesTransferAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amountValue)
        );
    }

    public static void checkStateChangesBurn(int metadataIndex, int dataIndex, int payIndex, Amount amount) {
        String assetId = amount.assetId().toString();
        long amountValue = amount.value();
        if (assetId != null) {
            assertThat(getStateChangesBurnAssetId(metadataIndex, dataIndex, payIndex)).isEqualTo(assetId);
        }
        assertThat(getStateChangesBurnAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amountValue);
    }

    public static void checkStateChangesReissue
            (int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        String assetId = data.getAssetAmount().assetId().toString();
        long amountValue = data.getAssetAmount().value();
        boolean reissue = Boolean.parseBoolean(data.getAssetData().get(REISSUE));
        if (assetId != null) {
            assertThat(getStateChangesReissueAssetId(metadataIndex, dataIndex, payIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getStateChangesReissueAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amountValue),
                () -> assertThat(getStateChangesReissueReissuable(metadataIndex, dataIndex, payIndex)).isEqualTo(reissue)
        );
    }

    public static void checkStateChangesData
            (int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        int intArg = data.getIntArg();
        assertAll(
                () -> assertThat(getStateChangesDataKey(metadataIndex, dataIndex, payIndex)).isEqualTo(DATA_ENTRY_INT),
                () -> assertThat(getStateChangesDataIntVal(metadataIndex, dataIndex, payIndex)).isEqualTo(intArg)
        );
    }

    public static void checkStateChangesSponsorFee
            (int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        assertAll(
                () -> assertThat(getStateChangesSponsorFeeAssetId(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(data.getAssetAmount().assetId().toString()),
                () -> assertThat(getStateChangesSponsorFeeAmount(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(data.getAssetAmount().value())
        );
    }

    public static void checkStateChangesLease
            (int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        assertAll(
                () -> assertThat(getStateChangesLeasesRecipientPkHash(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(data.getDAppPublicKeyHash()),
                () -> assertThat(getStateChangesLeasesAmount(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(data.getWavesAmount().value())
        );
    }

    public static void checkStateChangesLeaseCancel(int metadataIndex, int dataIndex, int payIndex) {
        String leaseId = getStateChangesLeasesId(metadataIndex, dataIndex, payIndex);
        assertThat(getStateChangesLeaseCancelsLeasesId(metadataIndex, dataIndex, payIndex)).isEqualTo(leaseId);
    }

}
