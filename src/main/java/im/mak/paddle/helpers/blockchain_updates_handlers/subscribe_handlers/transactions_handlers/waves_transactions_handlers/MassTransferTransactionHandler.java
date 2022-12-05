package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getWavesTransactionAtIndex;

public class MassTransferTransactionHandler {
    public static String getAttachmentFromMassTransfer(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex).getMassTransfer().getAttachment().toByteArray());
    }

    public static String getAssetIdFromMassTransfer(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex).getMassTransfer().getAssetId().toByteArray());
    }

    public static long getRecipientAmountFromMassTransfer(int txIndex, int transferIndex) {
        return getWavesTransactionAtIndex(txIndex).getMassTransfer().getTransfers(transferIndex).getAmount();
    }


    public static String getRecipientPublicKeyHashFromMassTransfer(int txIndex, int transferIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getMassTransfer()
                .getTransfers(transferIndex)
                .getRecipient()
                .getPublicKeyHash()
                .toByteArray());
    }
}
