package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata;

import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;

public class TransactionMetadataHandler {
    public static TransactionMetadata getElementTransactionMetadata(int metadataIndex) {
        return getAppend().getTransactionsMetadata(metadataIndex);
    }

    public static String getSenderAddressMetadata(int metadataIndex) {
        return Base58.encode(getElementTransactionMetadata(metadataIndex).getSenderAddress().toByteArray());
    }

    public static String getLeaseTransactionMetadata(int metadataIndex) {
        return Base58.encode(getElementTransactionMetadata(metadataIndex)
                .getLease()
                .getRecipientAddress()
                .toByteArray());
    }

    public static String getTransferRecipientAddressFromTransactionMetadata(int index) {
        return Base58.encode(getElementTransactionMetadata(index)
                .getTransfer()
                .getRecipientAddress()
                .toByteArray());
    }

    public static String getMassTransferFromTransactionMetadata(int index, int addressIndex) {
        return Base58.encode(getElementTransactionMetadata(index)
                .getMassTransfer()
                .getRecipientsAddresses(addressIndex)
                .toByteArray());
    }
}
