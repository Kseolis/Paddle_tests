package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getWavesTransactionAtIndex;

public class UpdateAssetInfoTransactionHandler {
    public static String getUpdateAssetInfoTransactionDescription(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getUpdateAssetInfo().getDescription();
    }

    public static String getUpdateAssetInfoTransactionName(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getUpdateAssetInfo().getName();
    }

    public static String getUpdateAssetInfoTransactionAssetId(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex).getUpdateAssetInfo().getAssetId().toByteArray());
    }
}
