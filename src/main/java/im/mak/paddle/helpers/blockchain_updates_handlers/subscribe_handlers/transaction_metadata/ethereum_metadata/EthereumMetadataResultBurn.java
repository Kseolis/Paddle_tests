package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResult;

public class EthereumMetadataResultBurn {
    public static String getEthereumInvokeBurnAssetId(int metadataIndex, int burnIndex) {
        return Base58.encode(getEthereumInvokeResult(metadataIndex)
                .getBurns(burnIndex)
                .getAssetId()
                .toByteArray());
    }

    public static long getEthereumInvokeBurnAmounts(int metadataIndex, int burnIndex) {
        return getEthereumInvokeResult(metadataIndex).getBurns(burnIndex).getAmount();
    }
}
