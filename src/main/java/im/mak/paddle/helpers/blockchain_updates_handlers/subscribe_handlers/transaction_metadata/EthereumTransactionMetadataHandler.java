package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.InvokeScriptMetadata;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.EthereumTransferMetadata;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.EthereumMetadata;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.TransactionMetadataHandler.getElementTransactionMetadata;

public class EthereumTransactionMetadataHandler {
    public static EthereumMetadata getEthereumTransactionMetadata(int metadataIndex) {
        return getElementTransactionMetadata(metadataIndex).getEthereum();
    }

    public static long getEthereumTransactionTimestampMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getTimestamp();
    }

    public static long getEthereumTransactionFeeMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getFee();
    }

    public static String getEthereumTransactionSenderPublicKeyMetadata(int metadataIndex) {
        return Base58.encode(getEthereumTransactionMetadata(metadataIndex).getSenderPublicKey().toByteArray());
    }

    public static String getEthereumTransferRecipientAddressMetadata(int metadataIndex) {
        return Base58.encode(getEthereumTransferTransactionMetadata(metadataIndex).getRecipientAddress().toByteArray());
    }

    public static long getEthereumTransferAmountMetadata(int metadataIndex) {
        return getEthereumTransferTransactionMetadata(metadataIndex).getAmount().getAmount();
    }

    private static EthereumTransferMetadata getEthereumTransferTransactionMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getTransfer();
    }

    private static InvokeScriptMetadata getEthereumInvokeTransactionMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getInvoke();
    }
}
