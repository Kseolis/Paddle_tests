package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResult;

public class EthereumInvokeMetadataSponsorFee {
    public static String getEthereumInvokeMetadataResultSponsorFeeAssetId(int metadataIndex, int subscribeIndex) {
        return Base58.encode(getEthereumInvokeResult(metadataIndex)
                .getSponsorFees(subscribeIndex)
                .getMinFee()
                .getAssetId()
                .toByteArray());
    }

    public static long getEthereumInvokeMetadataResultSponsorFeeAmount(int metadataIndex, int subscribeIndex) {
        return getEthereumInvokeResult(metadataIndex).getSponsorFees(subscribeIndex).getMinFee().getAmount();
    }
}
