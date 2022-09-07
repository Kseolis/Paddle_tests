package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataResultReissue extends BaseInvokeMetadata {

    public static String getInvokeMetadataResultReissueAssetId(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex).getReissues(dataIndex).getAssetId().toByteArray());
    }

    public static long getInvokeMetadataResultReissueAmount(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getReissues(dataIndex).getAmount();
    }

    public static boolean getInvokeMetadataResultReissueIsReissuable(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getReissues(dataIndex).getIsReissuable();
    }
}
