package im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers;

import im.mak.paddle.blockchain_updates.BaseGrpcTest;


import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeTransactionAssertions extends BaseGrpcTest {
    public static void checkInvokeSubscribeTransaction(long fee, String senderPublicKey, String txId, int txIndex) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getInvokeTransactionPublicKeyHash(txIndex)).isEqualTo(getDAppAccountPublicKeyHash()),
                //  () -> assertThat(getInvokeTransactionFunctionCall(txIndex)).isEqualTo();
                () -> assertThat(getTxId(txIndex)).isEqualTo(txId)
        );
    }

    public static void checkPaymentsSubscribe(int txIndex, int paymentIndex, long amount, String assetId) {
        if (assetId.isEmpty()) {
            assertThat(getInvokeTransactionPaymentAssetId(txIndex, paymentIndex)).isEqualTo(assetId);
        }
        assertThat(getInvokeTransactionPaymentAmount(txIndex, paymentIndex)).isEqualTo(amount);
    }
}
