package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class LeaseTransactionHandler {
    public static String getLeaseTransactionPublicKeyHash(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getLease()
                .getRecipient()
                .getPublicKeyHash()
                .toByteArray());
    }

    public static long getLeaseAssetAmount(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getLease().getAmount();
    }
}
