package im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers;

import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.helpers.PrepareInvokeTestsData;

import java.util.Map;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataArgs.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataResult.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataResultData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataResultStateChanges.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeResultIssues.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumMetadataResultBurn.getEthereumInvokeBurnAmounts;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumMetadataResultBurn.getEthereumInvokeBurnAssetId;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.NONCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EthereumInvokeMetadataAssertions {
    public static void checkEthereumInvokeIssueAssetMetadata(int metadataIndex, int dataIndex, Map<String, String> assetData) {
        assertAll(
                () -> assertThat(getEthereumInvokeIssuesName(metadataIndex, dataIndex))
                        .isEqualTo(assetData.get(NAME)),
                () -> assertThat(getEthereumInvokeIssuesDescription(metadataIndex, dataIndex))
                        .isEqualTo(assetData.get(DESCRIPTION)),
                () -> assertThat(getEthereumInvokeIssuesAmount(metadataIndex, dataIndex))
                        .isEqualTo(Long.valueOf(assetData.get(VOLUME))),
                () -> assertThat(getEthereumInvokeIssuesDecimals(metadataIndex, dataIndex))
                        .isEqualTo(Long.parseLong(assetData.get(DECIMALS))),
                () -> assertThat(getEthereumInvokeIssuesReissuable(metadataIndex, dataIndex))
                        .isEqualTo(Boolean.parseBoolean(assetData.get(REISSUE)))
        );
        if (assetData.get(NONCE) != null) {
            assertThat(getEthereumInvokeIssuesNonce(metadataIndex, dataIndex))
                    .isEqualTo(Long.parseLong(assetData.get(NONCE)));
        }
    }

    public static void checkArgumentsEthereumMetadata(int metadataIndex, int dataIndex, String argType, String argValue) {
        switch (argType) {
            case BINARY_BASE58:
                assertThat(getBinaryValueBase58ArgumentEthereumMetadata(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BINARY_BASE64:
                assertThat(getBinaryValueBase64ArgumentEthereumMetadata(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case INTEGER:
                assertThat(getIntegerArgumentEthereumMetadata(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case STRING:
                assertThat(getStringArgumentEthereumMetadata(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BOOLEAN:
                assertThat(getBooleanArgumentEthereumMetadata(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            default:
                break;
        }
    }


    public static void checkEthereumDataMetadata(int metadataIndex, int dataIndex, String type, String key, String argValue) {
        switch (type) {
            case BINARY_BASE58:
                assertThat(getEthereumInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getEthereumInvokeMetadataResultDataBinaryBase58Value(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BINARY_BASE64:
                assertThat(getEthereumInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getEthereumInvokeMetadataResultDataBinaryBase64Value(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case INTEGER:
                assertThat(getEthereumInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getEthereumInvokeMetadataResultDataIntegerValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case STRING:
                assertThat(getEthereumInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getEthereumInvokeMetadataResultDataStringValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            case BOOLEAN:
                assertThat(getEthereumInvokeMetadataResultDataKey(metadataIndex, dataIndex)).isEqualTo(key);
                assertThat(getEthereumInvokeMetadataResultDataBoolValue(metadataIndex, dataIndex)).isEqualTo(argValue);
                break;
            default:
                break;
        }
    }

    public static void checkEthereumInvokeBurnMetadata(int metadataIndex, int dataIndex, Amount amount) {
        if (amount.assetId() != null) {
            assertThat(getEthereumInvokeBurnAssetId(metadataIndex, dataIndex)).isEqualTo(amount.assetId().toString());
        }
        assertThat(getEthereumInvokeBurnAmounts(metadataIndex, dataIndex)).isEqualTo(amount.value());
    }

    public static void checkEthereumResultInvokesMetadataPayments(int metadataIndex, int dataIndex, int payIndex, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getEthereumInvokeMetadataResultInvokesPaymentAssetId(metadataIndex, dataIndex, payIndex)).isEqualTo(assetId);
        }
        assertThat(getEthereumInvokeMetadataResultInvokesPaymentAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amount);
    }

    public static void checkEthereumResultInvokesMetadata(int metadataIndex, int dataIndex, String dApp, String func) {
        assertAll(
                () -> assertThat(getEthereumInvokeMetadataResultInvokesDApp(metadataIndex, dataIndex)).isEqualTo(dApp),
                () -> assertThat(getEthereumInvokeMetadataResultInvokesDAppCallFunc(metadataIndex, dataIndex)).isEqualTo(func)
        );
    }

    public static void checkEthereumStateChangeIntData(int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        int intArg = data.getIntArg();
        assertAll(
                () -> assertThat(getEthereumStateChangesDataKey(metadataIndex, dataIndex, payIndex)).isEqualTo(DATA_ENTRY_INT),
                () -> assertThat(getEthereumStateChangesIntegerValue(metadataIndex, dataIndex, payIndex)).isEqualTo(intArg)
        );
    }

    public static void checkEthereumStateChangesBurn(int metadataIndex, int dataIndex, int payIndex, Amount amount) {
        String assetId = amount.assetId().toString();
        long amountValue = amount.value();
        if (assetId != null) {
            assertThat(getEthereumStateChangesBurnAssetId(metadataIndex, dataIndex, payIndex)).isEqualTo(assetId);
        }
        assertThat(getEthereumStateChangesBurnAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amountValue);
    }

    public static void checkEthereumStateChangesReissue(int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        String assetId = data.getAssetAmount().assetId().toString();
        long amountValue = data.getAssetAmount().value();
        boolean reissue = Boolean.parseBoolean(data.getAssetData().get(REISSUE));
        if (assetId != null) {
            assertThat(getEthereumStateChangesReissueAssetId(metadataIndex, dataIndex, payIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getEthereumStateChangesReissueAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(amountValue),
                () -> assertThat(getEthereumStateChangesReissueReissuable(metadataIndex, dataIndex, payIndex)).isEqualTo(reissue)
        );
    }

    public static void checkEthereumStateChangesSponsorFee(int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        assertAll(
                () -> assertThat(getEthereumStateChangesSponsorFeesAssetId(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(data.getAssetAmount().assetId().toString()),
                () -> assertThat(getEthereumStateChangesSponsorFeesAmount(metadataIndex, dataIndex, payIndex))
                        .isEqualTo(data.getAssetAmount().value())
        );
    }

    public static void checkEthereumStateChangesLease(int metadataIndex, int dataIndex, int payIndex, PrepareInvokeTestsData data) {
        assertAll(
                () -> assertThat(getEthereumStateChangesLeaseRecipientPKHash(metadataIndex, dataIndex, payIndex)).isEqualTo(data.getDAppPublicKeyHash()),
                () -> assertThat(getEthereumStateChangesLeaseAmount(metadataIndex, dataIndex, payIndex)).isEqualTo(data.getWavesAmount().value())
        );
    }

    public static void checkEthereumStateChangesLeaseCancel(int metadataIndex, int dataIndex, int leaseIndex) {
        String leaseId = getEthereumStateChangesLeaseId(metadataIndex, dataIndex, leaseIndex);
        assertThat(getEthereumStateChangesLeaseCancelsLeasesId(metadataIndex, dataIndex, leaseIndex)).isEqualTo(leaseId);
    }

    public static void checkEthereumStateChangesTransfers(int metadataIndex, int dataIndex, int transferIndex, String assetId, long amountValue, String address) {
        if (assetId != null) {
            assertThat(getEthereumStateChangesTransfersAmountAssetId(metadataIndex, dataIndex, transferIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getEthereumStateChangesTransfersAmountAddress(metadataIndex, dataIndex, transferIndex)).isEqualTo(address),
                () -> assertThat(getEthereumStateChangesTransfersAmountValue(metadataIndex, dataIndex, transferIndex)).isEqualTo(amountValue)
        );
    }
}
