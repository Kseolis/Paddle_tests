package im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers;

import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.helpers.PrepareInvokeTestsData;

import java.util.Map;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataArgs.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataPayment.getEthereumInvokeMetadataPaymentsAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataPayment.getEthereumInvokeMetadataPaymentsAssetId;
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
                () -> assertThat(getEthereumInvokeIssuesName(metadataIndex, dataIndex)).isEqualTo(assetData.get(NAME)),
                () -> assertThat(getEthereumInvokeIssuesDescription(metadataIndex, dataIndex)).isEqualTo(assetData.get(DESCRIPTION)),
                () -> assertThat(getEthereumInvokeIssuesAmount(metadataIndex, dataIndex)).isEqualTo(Long.valueOf(assetData.get(VOLUME))),
                () -> assertThat(getEthereumInvokeIssuesDecimals(metadataIndex, dataIndex)).isEqualTo(Long.parseLong(assetData.get(DECIMALS))),
                () -> assertThat(getEthereumInvokeIssuesReissuable(metadataIndex, dataIndex)).isEqualTo(Boolean.parseBoolean(assetData.get(REISSUE)))
        );
        if (assetData.get(NONCE) != null) {
            assertThat(getEthereumInvokeIssuesNonce(metadataIndex, dataIndex)).isEqualTo(Long.parseLong(assetData.get(NONCE)));
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

    public static void checkEthereumPaymentMetadata(int metadataIndex, int paymentIndex, String assetId, long amount) {
        if (assetId != null) {
            assertThat(getEthereumInvokeMetadataPaymentsAssetId(metadataIndex, paymentIndex)).isEqualTo(assetId);
        }
        assertThat(getEthereumInvokeMetadataPaymentsAmount(metadataIndex, paymentIndex)).isEqualTo(amount);
    }
}
