package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResult;

public class EthereumInvokeMetadataResultReissue {
    public static String getEthereumInvokeReissueAssetId(int metadataIndex, int reissueIndex) {
        return Base58.encode(getEthereumInvokeResult(metadataIndex)
                .getReissues(reissueIndex)
                .getAssetId()
                .toByteArray());
    }

    public static long getEthereumInvokeReissueAmounts(int metadataIndex, int reissueIndex) {
        return getEthereumInvokeResult(metadataIndex).getReissues(reissueIndex).getAmount();
    }

    public static boolean getEthereumInvokeReissueReissuable(int metadataIndex, int reissueIndex) {
        return getEthereumInvokeResult(metadataIndex).getReissues(reissueIndex).getIsReissuable();
    }
}
