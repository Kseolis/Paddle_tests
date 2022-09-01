/*
package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkMainMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkPaymentMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataCancelLeaseId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeLeaseCancelTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testData;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with LeaseCancel and WAVES payment")
    void subscribeInvokeWithLeaseCancel() {
        long amountValue = testData.getWavesAmount().value();

        testData.prepareDataForLeaseCancelTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testData.getCallerAccount(), testData.getDAppAccount(), testData.getDAppCall(), testData.getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(testData.getCallerAccount(), testData.getDAppAccount(), testData.getAmounts(), testData.getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testData.getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testData.getDAppAccount(), testData);

        assertionsCheck(amountValue);
    }

    private void assertionsCheck(long amountValue) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkPaymentsSubscribe(0, 0, amountValue, ""),
                () -> checkPaymentMetadata(0, 0, null, amountValue),
                () -> assertThat(getInvokeMetadataCancelLeaseId(0, 0)).isEqualTo(testData.getLeaseId()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getDAppAddress(),
                        null,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBeforeLeasing(0, 0, testData.getCallerAddress(), amountValue, 0),
                () -> checkStateUpdateBeforeLeasing(0, 1, testData.getDAppAddress(), 0, amountValue),

                () -> checkStateUpdateAfterLeasing(0, 0, testData.getCallerAddress(), 0, 0),
                () -> checkStateUpdateAfterLeasing(0, 1, testData.getDAppAddress(), 0, 0),

                () -> checkStateUpdateIndividualLeases(0, 0,
                        amountValue,
                        testData.getDAppPublicKey(),
                        testData.getCallerAddress(),
                        INACTIVE_STATUS_LEASE)
        );
    }
}
*/
