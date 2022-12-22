package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumTransactionMetadata.getEthereumTransactionMetadata;

public class EthereumTransferMetadata {
    public static String getEthereumTransferRecipientMetadataAddress(int metadataIndex) {
        return Base58.encode(getEthereumTransferTransactionMetadata(metadataIndex).getRecipientAddress().toByteArray());
    }

    public static long getEthereumTransferMetadataAmount(int metadataIndex) {
        return getEthereumTransferTransactionMetadata(metadataIndex).getAmount().getAmount();
    }

    public static String getEthereumTransferMetadataAssetId(int metadataIndex) {
        return Base58.encode(getEthereumTransferTransactionMetadata(metadataIndex).getAmount().getAssetId().toByteArray());
    }

    private static Events.TransactionMetadata.EthereumTransferMetadata getEthereumTransferTransactionMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getTransfer();
    }
}
