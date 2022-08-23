package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class TransferTransactionHandler {
    public static String getTransferTransactionPublicKeyHash(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getTransfer()
                .getRecipient()
                .getPublicKeyHash()
                .toByteArray());
    }

    public static long getTransferAssetAmount(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getTransfer().getAmount().getAmount();
    }

    public static String getTransferAssetId(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getTransfer()
                .getAmount()
                .getAssetId()
                .toByteArray());
    }
}
