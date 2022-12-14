package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.checkDataMetadata;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDeleteEntryGrpcTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private String intVal;
    private String intValueAfter;
    private AssetId assetId;
    private DAppCall dAppCall;
    private Account caller;
    private Address callerAddress;
    private Account dAppAccount;
    private Address dAppAddress;
    private List<Amount> payments;
    private long payment;

    @BeforeEach
    void before() {
        async(
                () -> {
                    testData = new PrepareInvokeTestsData();
                    testData.prepareDataForDeleteEntryTests();
                    calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
                    assetId = testData.getAssetId();
                    dAppCall = testData.getDAppCall();
                    caller = testData.getCallerAccount();
                    callerAddress = caller.address();
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    payments = testData.getPayments();
                    payment = testData.getWavesAmount().value();
                    intVal = String.valueOf(testData.getIntArg());

                },
                () -> intValueAfter = String.valueOf(0),
                () -> fromHeight = node().getHeight(),
                () -> setVersion(LATEST_VERSION)
        );
    }

    @Test
    @DisplayName("subscribe invoke with DeleteEntry")
    void subscribeInvokeWithDeleteEntry() {
        calcBalances.balancesAfterPaymentInvoke(callerAddress, dAppAddress, payments, assetId);
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
        txSender.invokeSenderWithPayment();
        String txId = txSender.getInvokeScriptId();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        prepareInvoke(dAppAccount, testData);
        assertionsCheck(getTxIndex(), txId);
    }

    private void assertionsCheck(int txIndex, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId, 0),
                () -> checkPaymentsSubscribe(txIndex, 0, payment, ""),

                () -> checkMainMetadata(txIndex),
                () -> checkArgumentsMetadata(txIndex, 0, INTEGER, intVal),
                () -> checkPaymentMetadata(txIndex, 0, null, payment),
                () -> checkDataMetadata(txIndex, 0, INTEGER, DATA_ENTRY_INT, intVal),

                () -> checkStateUpdateBalance(txIndex,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        1,
                        getDAppAccountAddress(),
                        "",
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(txIndex, 0, getDAppAccountAddress(), DATA_ENTRY_INT, intValueAfter)
        );
    }
}
