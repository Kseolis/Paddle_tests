package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getWavesTransactionAtIndex;

public class BurnTransactionHandler {
    public static long getBurnAssetAmount(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getBurn().getAssetAmount().getAmount();
    }

    public static String getBurnAssetId(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getBurn()
                .getAssetAmount()
                .getAssetId()
                .toByteArray());
    }
}
