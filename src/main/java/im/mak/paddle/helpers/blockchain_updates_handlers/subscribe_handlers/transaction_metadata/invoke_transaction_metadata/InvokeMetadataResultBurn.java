package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataResultBurn extends BaseInvokeMetadata {

    public static String getInvokeMetadataResultBurnAssetId(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex).getBurns(dataIndex).getAssetId().toByteArray());
    }

    public static long getInvokeMetadataResultBurnAmount(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getBurns(dataIndex).getAmount();
    }
}
