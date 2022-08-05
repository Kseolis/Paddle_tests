package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataResultSponsorFee extends BaseInvokeMetadata {
    public static String getInvokeMetadataResultSponsorFeeAssetId(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex)
                .getSponsorFees(dataIndex)
                .getMinFee()
                .getAssetId()
                .toByteArray());
    }

    public static long getInvokeMetadataResultSponsorFeeAmount(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getSponsorFees(dataIndex).getMinFee().getAmount();
    }
}
