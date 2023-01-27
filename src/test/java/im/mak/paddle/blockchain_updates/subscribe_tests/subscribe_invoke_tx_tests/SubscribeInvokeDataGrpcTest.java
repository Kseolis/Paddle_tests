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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDataGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private DAppCall dAppCall;
    private String dAppFunctionName;
    private Account caller;
    private Address callerAddress;
    private String callerAddressString;
    private String callerPublicKey;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressString;
    private String dAppPKHash;
    private List<Amount> payments;
    private long payment;
    private String intVal;
    private String binVal;
    private String boolArg;
    private String strVal;
    private long invokeFee;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForDataDAppTests(SUM_FEE, ONE_WAVES);
        AssetId assetId = testData.getAssetId();
        async(
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppFunctionName = dAppCall.getFunction().name();
                },
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = caller.address();
                    callerAddressString = testData.getCallerAddress();
                    callerPublicKey = testData.getCallerPublicKey();
                },
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAddressString = testData.getDAppAddress();
                    dAppPKHash = Base58.encode(dAppAccount.address().publicKeyHash());
                },
                () -> intVal = String.valueOf(testData.getIntArg()),
                () -> binVal = String.valueOf(testData.getBase64String()),
                () -> boolArg = String.valueOf(testData.getBoolArg()),
                () -> strVal = testData.getStringArg(),
                () -> invokeFee = testData.getInvokeFee(),
                () -> payments = testData.getPayments(),
                () -> payment = testData.getWavesAmount().value()
        );

        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(callerAddress, dAppAddress, payments, assetId);
    }

    @Test
    @DisplayName("subscribe invoke with DataDApp")
    void subscribeInvokeWithDataDApp() {
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
        txSender.invokeSenderWithPayment(LATEST_VERSION);
        String txId = txSender.getInvokeScriptId();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        assertionsCheck(txId, getTxIndex());
    }

    private void assertionsCheck(String txId, int txIndex) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPublicKey, txId, 0, dAppPKHash),
                () -> checkPaymentsSubscribe(txIndex, 0, payment, WAVES_STRING_ID),
                () -> checkMainMetadata(txIndex, dAppAddressString, dAppFunctionName),
                () -> checkArgumentsMetadata(txIndex, 0, INTEGER, intVal),
                () -> checkArgumentsMetadata(txIndex, 1, BINARY_BASE64, binVal),
                () -> checkArgumentsMetadata(txIndex, 2, BOOLEAN, boolArg),
                () -> checkArgumentsMetadata(txIndex, 3, STRING, strVal),

                () -> checkPaymentMetadata(txIndex, 0, null, payment),

                () -> checkDataMetadata(txIndex, 0, INTEGER, DATA_ENTRY_INT, intVal),
                () -> checkDataMetadata(txIndex, 1, BINARY_BASE64, DATA_ENTRY_BYTE, binVal),
                () -> checkDataMetadata(txIndex, 2, BOOLEAN, DATA_ENTRY_BOOL, boolArg),
                () -> checkDataMetadata(txIndex, 3, STRING, DATA_ENTRY_STR, strVal),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddressString, DATA_ENTRY_INT, intVal),
                () -> checkStateUpdateDataEntries(txIndex, 1, dAppAddressString, DATA_ENTRY_BYTE, binVal),
                () -> checkStateUpdateDataEntries(txIndex, 2, dAppAddressString, DATA_ENTRY_BOOL, boolArg),
                () -> checkStateUpdateDataEntries(txIndex, 3, dAppAddressString, DATA_ENTRY_STR, strVal),

                () -> checkStateUpdateBalance(txIndex,
                        0,
                        callerAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        1,
                        dAppAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction())
        );
    }
}
