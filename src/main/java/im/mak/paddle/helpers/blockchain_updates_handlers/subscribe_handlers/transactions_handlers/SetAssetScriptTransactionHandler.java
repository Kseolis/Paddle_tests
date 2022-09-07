package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class SetAssetScriptTransactionHandler {
    public static String getAssetIdFromSetAssetScript(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getSetAssetScript()
                .getAssetId()
                .toByteArray());
    }

    public static byte[] getScriptFromSetAssetScript(int txIndex) {
        return getWavesTransactionAtIndex(txIndex)
                .getSetAssetScript()
                .getScript()
                .toByteArray();
    }
}
