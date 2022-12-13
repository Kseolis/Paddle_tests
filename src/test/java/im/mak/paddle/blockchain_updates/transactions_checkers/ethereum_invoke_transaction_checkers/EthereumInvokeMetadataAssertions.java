package im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers;

import com.wavesplatform.transactions.common.Amount;

import java.util.Map;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeIssuesNonce;
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

    public static void checkEthereumInvokeBurnMetadata(int metadataIndex, int dataIndex, Amount amount) {
        if (amount.assetId() != null) {
            assertThat(getEthereumInvokeBurnAssetId(metadataIndex, dataIndex)).isEqualTo(amount.assetId().toString());
        }
        assertThat(getEthereumInvokeBurnAmounts(metadataIndex, dataIndex)).isEqualTo(amount.value());
    }
}
