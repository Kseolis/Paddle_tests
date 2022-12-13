package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;
import com.wavesplatform.protobuf.AmountOuterClass.Amount;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResultInvokes;

public class EthereumInvokeMetadataResultStateChanges {

    public static String getEthereumStateChangesKey(int metadataIndex, int invokeIndex, int dataIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getData(dataIndex).getKey();
    }

    public static String getEthereumStateChangesStringValue(int metadataIndex, int invokeIndex, int dataIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getData(dataIndex).getStringValue();
    }

    public static String getEthereumStateChangesBase58Value(int metadataIndex, int invokeIndex, int dataIndex) {
        return Base58.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getData(dataIndex).getBinaryValue().toByteArray());
    }

    public static String getEthereumStateChangesBase64Value(int metadataIndex, int invokeIndex, int dataIndex) {
        return Base64.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getData(dataIndex).getBinaryValue().toByteArray());
    }

    public static long getEthereumStateChangesIntegerValue(int metadataIndex, int invokeIndex, int dataIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getData(dataIndex).getIntValue();
    }

    public static boolean getEthereumStateChangesBooleanValue(int metadataIndex, int invokeIndex, int dataIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getData(dataIndex).getBoolValue();
    }

    public static Amount getEthereumStateChangesAmount(int metadataIndex, int invokeIndex, int transferIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getTransfers(transferIndex).getAmount();
    }

    public static long getEthereumStateChangesReissueAmount(int metadataIndex, int invokeIndex, int reissueIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getReissues(reissueIndex).getAmount();
    }

    public static String getEthereumStateChangesReissueAssetId(int metadataIndex, int invokeIndex, int reissueIndex) {
        return Base58.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getReissues(reissueIndex).getAssetId().toByteArray());
    }

    public static long getEthereumStateChangesBurnAmount(int metadataIndex, int invokeIndex, int burnIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getBurns(burnIndex).getAmount();
    }

    public static String getEthereumStateChangesBurnAssetId(int metadataIndex, int invokeIndex, int burnIndex) {
        return Base58.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getBurns(burnIndex).getAssetId().toByteArray());
    }

    public static long getEthereumStateChangesSponsorFeesAmount(int metadataIndex, int invokeIndex, int sponsorFeesIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getSponsorFees(sponsorFeesIndex).getMinFee().getAmount();
    }

    public static String getEthereumStateChangesSponsorFeesAssetId(int metadataIndex, int invokeIndex, int sponsorFeesIndex) {
        return Base58.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex)
                .getSponsorFees(sponsorFeesIndex)
                .getMinFee()
                .getAssetId()
                .toByteArray());
    }

    public static String getEthereumStateChangesLeaseRecipientPKHash(int metadataIndex, int invokeIndex, int leasesIndex) {
        return Base58.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex)
                .getLeases(leasesIndex)
                .getRecipient()
                .getPublicKeyHash()
                .toByteArray());
    }

    public static long getEthereumStateChangesLeaseAmount(int metadataIndex, int invokeIndex, int burnIndex) {
        return getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex).getLeases(burnIndex).getAmount();
    }

    public static String getEthereumStateChangesLeaseId(int metadataIndex, int invokeIndex, int burnIndex) {
        return Base58.encode(getEthereumInvokeMetadataResultStateChanges(metadataIndex, invokeIndex)
                .getLeases(burnIndex)
                .getLeaseId()
                .toByteArray()
        );
    }

    static InvokeScriptResult getEthereumInvokeMetadataResultStateChanges(int metadataIndex, int invokeIndex) {
        return getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getStateChanges();
    }
}
