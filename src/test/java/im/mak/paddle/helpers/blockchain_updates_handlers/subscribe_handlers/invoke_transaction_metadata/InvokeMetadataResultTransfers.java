package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataResultTransfers extends BaseInvokeMetadata {

    public static String getInvokeMetadataResultTransfersAssetId(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex)
                .getTransfers(dataIndex)
                .getAmount()
                .getAssetId()
                .toByteArray());
    }

    public static long getInvokeMetadataResultTransfersAmount(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getTransfers(dataIndex).getAmount().getAmount();
    }

    public static String getInvokeMetadataResultTransfersAddress(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex)
                .getTransfers(dataIndex)
                .getAddress()
                .toByteArray());
    }
}
