package im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers;

import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.helpers.PrepareInvokeTestsData;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataResultStateChanges.*;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EthereumInvokeStateUpdateAssertions {
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
