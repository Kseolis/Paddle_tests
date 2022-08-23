package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class InvokeTransactionHandler {
    public static String getInvokeTransactionPublicKeyHash(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getInvokeScript()
                .getDApp()
                .getPublicKeyHash().toByteArray()
        );
    }

    public static String getInvokeTransactionFunctionCall(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getInvokeScript()
                .getFunctionCall()
                .toByteArray());
    }

    public static long getInvokeTransactionPaymentAmount(int txIndex, int paymentIndex) {
        long amount;
        try {
            amount = getWavesTransactionAtIndex(txIndex)
                    .getInvokeScript()
                    .getPayments(paymentIndex)
                    .getAmount();
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
        return amount;
    }

    public static String getInvokeTransactionPaymentAssetId(int txIndex, int paymentIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getInvokeScript()
                .getPayments(paymentIndex)
                .getAssetId()
                .toByteArray());
    }
}
