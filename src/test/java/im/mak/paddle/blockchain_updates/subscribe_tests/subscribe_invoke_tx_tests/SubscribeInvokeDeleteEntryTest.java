/*
package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkDataMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDeleteEntryTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testsData;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with DeleteEntry")
    void subscribeInvokeWithDeleteEntry() {
        String intValueAfter = String.valueOf(0);
        testsData.prepareDataForDeleteEntryTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testsData.getCallerAccount(), testsData.getDAppAccount(), testsData.getDAppCall(), testsData.getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(testsData.getCallerAccount(), testsData.getDAppAccount(), testsData.getAmounts(), testsData.getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testsData.getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testsData.getDAppAccount(), testsData);

        assertionsCheck(testsData.getWavesAmount().value(), String.valueOf(testsData.getIntArg()), intValueAfter);
    }

    private void assertionsCheck(long payment, String intVal, String valAfter) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testsData.getInvokeFee(), testsData.getCallerPublicKey()),
                () -> checkPaymentsSubscribe(0, 0, payment, ""),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, INTEGER, intVal),
                () -> checkPaymentMetadata(0, 0, null, payment),
                () -> checkDataMetadata(0, 0, INTEGER, DATA_ENTRY_INT, intVal),

                () -> checkStateUpdateBalance(0,
                        0,
                        testsData.getCallerAddress(),
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
*/
