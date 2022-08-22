package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkMainMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkPaymentMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataCancelLeaseId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeLeaseCancelTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with LeaseCancel and WAVES payment")
    void subscribeInvokeWithLeaseCancel() {
        long amountValue = getWavesAmount().value();

        getTestsData().prepareDataForLeaseCancelTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getDAppAccount());

        assertionsCheck(amountValue);
    }

    private void assertionsCheck(long amountValue) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(SUM_FEE, getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkPaymentsSubscribe(0, 0, amountValue, ""),
                () -> checkPaymentMetadata(0, 0, null, amountValue),
                () -> assertThat(getInvokeMetadataCancelLeaseId(0, 0)).isEqualTo(getLeaseId()),

                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getDAppAddress(),
                        null,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBeforeLeasing(0, 0, getCallerAddress(), amountValue, 0),
                () -> checkStateUpdateBeforeLeasing(0, 1, getDAppAddress(), 0, amountValue),

                () -> checkStateUpdateAfterLeasing(0, 0, getCallerAddress(), 0, 0),
                () -> checkStateUpdateAfterLeasing(0, 1, getDAppAddress(), 0, 0),

                () -> checkStateUpdateIndividualLeases(0, 0,
                        amountValue,
                        getDAppPublicKey(),
                        getCallerAddress(),
                        INACTIVE_STATUS_LEASE)
        );
    }
}
