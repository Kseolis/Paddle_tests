package im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkDataMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.blockchain_updates.invoke_subscribe_helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.blockchain_updates.invoke_subscribe_helpers.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDeleteEntryTest extends SubscribeInvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with DeleteEntry")
    void subscribeInvokeWithDeleteEntry() {
        String intValueAfter = String.valueOf(0);
        prepareDataForDeleteEntryTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getDAppAccount());

        assertionsCheck(getWavesAmount().value(), String.valueOf(getIntArg()), intValueAfter);
    }

    private void assertionsCheck(long payment, String intVal, String valAfter) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(getInvokeFee(), getCallerPublicKey()),
                () -> checkPaymentsSubscribe(0, 0, payment, ""),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, INTEGER, intVal),
                () -> checkPaymentMetadata(0, 0, null, payment),
                () -> checkDataMetadata(0, 0, INTEGER, DATA_ENTRY_INT, intVal),

                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getDAppAccountAddress(),
                        "",
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),


                () -> checkStateUpdateDataEntries(0, 0, getDAppAccountAddress(), DATA_ENTRY_INT, valAfter)
        );
    }
}
