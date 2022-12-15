package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Lease;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResult;

public class EthereumInvokeMetadataResultLease {
    public static String getEthereumInvokeMetadataLeasesRecipientPublicKey(int metadataIndex, int leaseIndex) {
        return Base58.encode(getEthereumInvokeMetadataLeases(metadataIndex, leaseIndex).getRecipient().getPublicKeyHash().toByteArray());
    }

    public static long getEthereumInvokeMetadataLeasesAmount(int metadataIndex, int leaseIndex) {
        return getEthereumInvokeMetadataLeases(metadataIndex, leaseIndex).getAmount();
    }

    public static String getEthereumInvokeMetadataLeaseId(int metadataIndex, int leaseIndex) {
        return Base58.encode(getEthereumInvokeMetadataLeases(metadataIndex, leaseIndex).getLeaseId().toByteArray());
    }

    public static byte[] getEthereumInvokeMetadataCancelLeaseId(int metadataIndex, int leaseIndex) {
        return getEthereumInvokeResult(metadataIndex).getLeaseCancels(leaseIndex).getLeaseId().toByteArray();
    }

    private static Lease getEthereumInvokeMetadataLeases(int metadataIndex, int leaseIndex) {
        return getEthereumInvokeResult(metadataIndex).getLeases(leaseIndex);
    }
}
