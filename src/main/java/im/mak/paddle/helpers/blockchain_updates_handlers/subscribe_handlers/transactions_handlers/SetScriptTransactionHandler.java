package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class SetScriptTransactionHandler {
    public static byte[] getScriptFromSetScript(int txIndex) {
        return getWavesTransactionAtIndex(txIndex)
                .getSetScript()
                .getScript()
                .toByteArray();
    }
}
