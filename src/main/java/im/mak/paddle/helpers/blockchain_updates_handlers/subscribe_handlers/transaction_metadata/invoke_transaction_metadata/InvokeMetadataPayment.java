package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataPayment extends BaseInvokeMetadata {
    public static long getInvokeMetadataPaymentsAmount(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getPayments(argIndex).getAmount();
    }

    public static String getInvokeMetadataPaymentsAssetId(int metadataIndex, int argIndex) {
        return Base58.encode(
                getInvokeMetadata(metadataIndex).getPayments(argIndex).getAssetId().toByteArray()
        );
    }
}
