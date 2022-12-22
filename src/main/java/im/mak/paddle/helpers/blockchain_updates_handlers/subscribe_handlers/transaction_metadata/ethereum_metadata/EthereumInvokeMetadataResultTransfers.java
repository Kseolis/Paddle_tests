package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Payment;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResult;

public class EthereumInvokeMetadataResultTransfers {
    public static String getEthereumInvokeMetadataResultTransfersAssetId(int metadataIndex, int transferIndex) {
        return Base58.encode(getEthereumInvokeResultTransfers(metadataIndex, transferIndex).getAmount().getAssetId().toByteArray());
    }

    public static long getEthereumInvokeMetadataResultTransfersAmount(int metadataIndex, int transferIndex) {
        return getEthereumInvokeResultTransfers(metadataIndex, transferIndex).getAmount().getAmount();
    }

    public static String getEthereumInvokeMetadataResultTransfersAddress(int metadataIndex, int transferIndex) {
        return Base58.encode(getEthereumInvokeResultTransfers(metadataIndex, transferIndex).getAddress().toByteArray());
    }

    private static Payment getEthereumInvokeResultTransfers(int metadataIndex, int transferIndex) {
        return getEthereumInvokeResult(metadataIndex).getTransfers(transferIndex);
    }
}
