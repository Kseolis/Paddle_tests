package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class ReissueTransactionHandler {
    public static long getReissueAssetAmount(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getReissue().getAssetAmount().getAmount();
    }

    public static String getReissueAssetId(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getReissue()
                .getAssetAmount()
                .getAssetId()
                .toByteArray());
    }
}
