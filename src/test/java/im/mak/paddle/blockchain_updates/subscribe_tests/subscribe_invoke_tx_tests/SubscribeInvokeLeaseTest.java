package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.invokeSenderWithPayment;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeLeaseTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with Lease and WAVES payment")
    void subscribeInvokeWithLease() {
        long amountValue = getWavesAmount().value();
        testsData.prepareDataForLeaseTests();

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
        invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

        height = node().getHeight();
        subscribeResponseHandler(channel, getDAppAccount(), height, height);
        prepareInvoke(getDAppAccount());

        assertionsCheck(amountValue);
    }

    private void assertionsCheck(long amountValue) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(SUM_FEE),
                () -> checkMainMetadata(0),
                () -> checkPaymentsSubscribe(amountValue),
                () -> checkPaymentMetadata(0, 0, amountValue),
                () -> checkLeaseMetadata(0, 0, getCallerPublicKeyHash(), amountValue),

                () -> checkStateUpdateBalance(0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(),
                        getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(1,
                        getDAppAddress(),
                        null,
                        getDAppBalanceWavesBeforeTransaction(),
                        getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBeforeLeasingForAddress(0, 0, getCallerAddress(), 0, 0),
                () -> checkStateUpdateBeforeLeasingForAddress(0, 1, getDAppAddress(), 0, 0),

                () -> checkStateUpdateAfterLeasingForAddress(0, 0, getCallerAddress(), amountValue, 0),
                () -> checkStateUpdateAfterLeasingForAddress(0, 1, getDAppAddress(), 0, amountValue),

                () -> checkStateUpdateIndividualLeases(0, 0,
                        amountValue,
                        getDAppPublicKey(),
                        getCallerAddress(),
                        ACTIVE_STATUS_LEASE)
        );
    }
}
