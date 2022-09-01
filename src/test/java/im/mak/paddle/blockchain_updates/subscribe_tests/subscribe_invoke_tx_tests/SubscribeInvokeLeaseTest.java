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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeLeaseTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testsData;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Lease and WAVES payment")
    void subscribeInvokeWithLease() {
        long amountValue = testsData.getWavesAmount().value();
        testsData.prepareDataForLeaseTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testsData.getCallerAccount(), testsData.getDAppAccount(), testsData.getDAppCall(), testsData.getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(testsData.getCallerAccount(), testsData.getDAppAccount(), testsData.getAmounts(), testsData.getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testsData.getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testsData.getDAppAccount(), testsData);

        assertionsCheck(amountValue);
    }

    private void assertionsCheck(long amountValue) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testsData.getInvokeFee(), testsData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkPaymentsSubscribe(0, 0, amountValue, ""),
                () -> checkPaymentMetadata(0, 0, null, amountValue),
                () -> checkLeaseMetadata(0, 0, testsData.getCallerPublicKeyHash(), amountValue),

                () -> checkStateUpdateBalance(0,
                        0,
                        testsData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testsData.getDAppAddress(),
                        null,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBeforeLeasing(0, 0, testsData.getCallerAddress(), 0, 0),
                () -> checkStateUpdateBeforeLeasing(0, 1, testsData.getDAppAddress(), 0, 0),

                () -> checkStateUpdateAfterLeasing(0, 0, testsData.getCallerAddress(), amountValue, 0),
                () -> checkStateUpdateAfterLeasing(0, 1, testsData.getDAppAddress(), 0, amountValue),

                () -> checkStateUpdateIndividualLeases(0, 0,
                        amountValue,
                        testsData.getDAppPublicKey(),
                        testsData.getCallerAddress(),
                        ACTIVE_STATUS_LEASE)
        );
    }
}
*/
