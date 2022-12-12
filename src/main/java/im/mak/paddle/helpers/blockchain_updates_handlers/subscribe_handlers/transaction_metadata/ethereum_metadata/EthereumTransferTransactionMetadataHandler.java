package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.EthereumTransferMetadata;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumTransactionMetadata.getEthereumTransactionMetadata;

public class EthereumTransferTransactionMetadataHandler {
    private static EthereumTransferMetadata getEthereumTransferTransactionMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getTransfer();
    }

    public static String getEthereumTransferRecipientAddressMetadata(int metadataIndex) {
        return Base58.encode(getEthereumTransferTransactionMetadata(metadataIndex).getRecipientAddress().toByteArray());
    }

    public static long getEthereumTransferAmountMetadata(int metadataIndex) {
        return getEthereumTransferTransactionMetadata(metadataIndex).getAmount().getAmount();
    }
}
