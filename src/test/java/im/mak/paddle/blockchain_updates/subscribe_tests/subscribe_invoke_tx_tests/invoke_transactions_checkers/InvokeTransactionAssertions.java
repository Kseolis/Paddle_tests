package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers;

import im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeBaseTest;


import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeTransactionAssertions extends InvokeBaseTest {
    public static void checkInvokeSubscribeTransaction(long fee) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(fee),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(getCallerAccount().publicKey().toString()),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(getDAppAccountPublicKeyHash()),
                //  () -> assertThat(getInvokeTransactionFunctionCall(0)).isEqualTo(getDAppCall().getFunction().toString());
                () -> assertThat(getTransactionId()).isEqualTo(getInvokeScriptId())
        );
    }

    public static void checkPaymentsSubscribe(long amount, String assetId) {
        if (assetId.isEmpty()) {
            assertThat(getInvokeTransactionPaymentAssetId(0, 0)).isEqualTo(assetId);
        }
        assertThat(getInvokeTransactionPaymentAmount(0, 0)).isEqualTo(amount);
    }
}
