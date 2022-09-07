package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class AliasTransactionHandler {
    public static String getAliasFromAliasTransaction(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getCreateAlias().getAlias();
    }
}
