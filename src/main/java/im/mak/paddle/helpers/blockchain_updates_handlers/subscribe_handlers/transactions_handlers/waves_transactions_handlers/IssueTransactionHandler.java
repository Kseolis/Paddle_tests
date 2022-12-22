package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getWavesTransactionAtIndex;

public class IssueTransactionHandler {
    public static String getAssetName(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getIssue().getName();
    }

    public static String getAssetDescription(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getIssue().getDescription();
    }

    public static long getAssetAmount(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getIssue().getAmount();
    }

    public static boolean getAssetReissuable(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getIssue().getReissuable();
    }

    public static int getAssetDecimals(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getIssue().getDecimals();
    }

    public static byte[] getAssetScript(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getIssue().getScript().toByteArray();
    }
}
