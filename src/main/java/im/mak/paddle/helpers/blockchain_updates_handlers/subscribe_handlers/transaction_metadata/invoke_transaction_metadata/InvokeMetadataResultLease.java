package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Lease;

public class InvokeMetadataResultLease extends BaseInvokeMetadata {
    public static String getInvokeMetadataLeasesRecipientPublicKey(int metadataIndex, int argIndex) {
        return Base58.encode(getInvokeMetadataLeases(metadataIndex, argIndex)
                .getRecipient()
                .getPublicKeyHash()
                .toByteArray()
        );
    }

    public static long getInvokeMetadataLeasesAmount(int metadataIndex, int argIndex) {
        return getInvokeMetadataLeases(metadataIndex, argIndex).getAmount();
    }

    public static String getInvokeMetadataLeaseId(int metadataIndex, int argIndex) {
        return Base58.encode(getInvokeMetadataLeases(metadataIndex, argIndex)
                .getLeaseId()
                .toByteArray()
        );
    }

    public static byte[] getInvokeMetadataCancelLeaseId(int metadataIndex, int argIndex) {
        return getInvokeScriptResult(metadataIndex)
                .getLeaseCancels(argIndex)
                .getLeaseId()
                .toByteArray();
    }

    private static Lease getInvokeMetadataLeases(int metadataIndex, int argIndex) {
        return getInvokeScriptResult(metadataIndex)
                .getLeases(argIndex);
    }
}
