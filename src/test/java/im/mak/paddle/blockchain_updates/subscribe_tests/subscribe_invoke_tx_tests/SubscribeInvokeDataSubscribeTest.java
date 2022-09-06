package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseSubscribeTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDataSubscribeTest extends BaseSubscribeTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with DataDApp")
    void subscribeInvokeWithDataDApp() {
        long payment = testData.getWavesAmount().value();
        testData.prepareDataForDataDAppTests();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        final AssetId assetId = testData.getAssetId();
        final DAppCall dAppCall = testData.getDAppCall();
        final Account caller = testData.getCallerAccount();
        final Account dAppAccount = testData.getDAppAccount();
        final List<Amount> amounts = testData.getAmounts();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (caller, dAppAccount, dAppCall, testData.getAmounts());

        setVersion(LATEST_VERSION);
        calcBalances.balancesAfterPaymentInvoke(caller, dAppAccount, amounts, assetId);
        txSender.invokeSenderWithPayment();

        final String txId = txSender.getInvokeScriptId();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, dAppAccount, height, height, txId);
        prepareInvoke(dAppAccount, testData);

        assertionsCheck(payment,
                String.valueOf(testData.getIntArg()),
                testData.getBase64String().toString(),
                String.valueOf(testData.getBoolArg()),
                testData.getStringArg(),
                txId
        );
    }

    private void assertionsCheck(long payment, String intVal, String binVal, String boolArg, String strVal, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId),
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
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        1,
                        getDAppAccountAddress(),
                        "",
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction())
        );
    }
}
