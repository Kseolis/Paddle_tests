package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
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
    private String dAppFunctionName;
    private Account caller;
    private Address callerAddress;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressStr;
    private String dAppPKHash;
    private List<Amount> payments;
    private long payment;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForDeleteEntryTests();
        async(
                () -> calcBalances = new InvokeCalculationsBalancesAfterTx(testData),
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppPKHash = Base58.encode(dAppAccount.address().publicKeyHash());
                    dAppAddress = dAppAccount.address();
                    dAppAddressStr = dAppAddress.toString();
                },
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = caller.address();
                },
                () -> payments = testData.getPayments(),
                () -> payment = testData.getWavesAmount().value(),
                () -> dAppCall = testData.getDAppCall(),
                () -> dAppFunctionName = testData.getDAppCall().getFunction().name(),
                () -> assetId = testData.getAssetId(),
                () -> intVal = String.valueOf(testData.getIntArg()),
                () -> intValueAfter = String.valueOf(0),
                () -> fromHeight = node().getHeight()
        );
    }

    @Test
    @DisplayName("subscribe invoke with DeleteEntry")
    void subscribeInvokeWithDeleteEntry() {
        calcBalances.balancesAfterPaymentInvoke(callerAddress, dAppAddress, payments, assetId);
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
        txSender.invokeSenderWithPayment(LATEST_VERSION);
        String txId = txSender.getInvokeScriptId();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheck(getTxIndex(), txId);
    }

    private void assertionsCheck(int txIndex, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId, txIndex, dAppPKHash),
                () -> checkPaymentsSubscribe(txIndex, 0, payment, ""),

                () -> checkMainMetadata(txIndex, dAppAddressStr, dAppFunctionName),
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
                        dAppAddressStr,
                        "",
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddressStr, DATA_ENTRY_INT, intValueAfter)
        );
    }
}
