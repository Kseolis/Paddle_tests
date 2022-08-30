package im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.blockchain_updates.invoke_subscribe_helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.blockchain_updates.invoke_subscribe_helpers.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDataTest extends SubscribeInvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with DataDApp")
    void subscribeInvokeWithDataDApp() {
        long payment = getWavesAmount().value();
        prepareDataForDataDAppTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getDAppAccount());

        assertionsCheck(payment,
                String.valueOf(getIntArg()),
                getBase64String().toString(),
                String.valueOf(getBoolArg()),
                getStringArg()
        );
    }

    private void assertionsCheck(long payment, String intVal, String binVal, String boolArg, String strVal) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(getInvokeFee(), getCallerPublicKey()),
                () -> checkPaymentsSubscribe(0, 0, payment, ""),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, INTEGER, intVal),
                () -> checkArgumentsMetadata(0, 1, BINARY_BASE64, binVal),
                () -> checkArgumentsMetadata(0, 2, BOOLEAN, boolArg),
                () -> checkArgumentsMetadata(0, 3, STRING, strVal),

                () -> checkPaymentMetadata(0, 0, null, payment),

                () -> checkDataMetadata(0, 0, INTEGER, DATA_ENTRY_INT, intVal),
                () -> checkDataMetadata(0, 1, BINARY_BASE64, DATA_ENTRY_BYTE, binVal),
                () -> checkDataMetadata(0, 2, BOOLEAN, DATA_ENTRY_BOOL, boolArg),
                () -> checkDataMetadata(0, 3, STRING, DATA_ENTRY_STR, strVal),

                () -> checkStateUpdateDataEntries(0, 0, getDAppAccountAddress(), DATA_ENTRY_INT, intVal),
                () -> checkStateUpdateDataEntries(0, 1, getDAppAccountAddress(), DATA_ENTRY_BYTE, binVal),
                () -> checkStateUpdateDataEntries(0, 2, getDAppAccountAddress(), DATA_ENTRY_BOOL, boolArg),
                () -> checkStateUpdateDataEntries(0, 3, getDAppAccountAddress(), DATA_ENTRY_STR, strVal),


                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getDAppAccountAddress(),
                        "",
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction())

        );
    }
}
