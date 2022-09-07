package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers;

import im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeBaseTest;


import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeTransactionAssertions extends InvokeBaseTest {
    public static void checkInvokeSubscribeTransaction(long fee, String senderPublicKey) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(fee),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(getDAppAccountPublicKeyHash()),
                //  () -> assertThat(getInvokeTransactionFunctionCall(0)).isEqualTo(getDAppCall().getFunction().toString());
                () -> assertThat(getTransactionId()).isEqualTo(getInvokeScriptId())
        );
    }

    public static void checkPaymentsSubscribe(int txIndex, int paymentIndex, long amount, String assetId) {
        if (assetId.isEmpty()) {
            assertThat(getInvokeTransactionPaymentAssetId(txIndex, paymentIndex)).isEqualTo(assetId);
        }
        assertThat(getInvokeTransactionPaymentAmount(txIndex, paymentIndex)).isEqualTo(amount);
    }
}
