package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDoubleNestedCallerTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private static final String callerForScript = "i.caller";
    private static final String originCallerForScript = "i.originCaller";
    private Account caller;
    private String callerAddress;
    private String callerPK;
    private Account dAppAccount;
    private String dAppPKHash;
    private String dAppFunctionName;
    private String dAppAddress;
    private Account otherDAppAccount;
    private String otherDAppAddress;
    private Account assetDAppAccount;
    private String assetDAppAddress;
    private AssetId assetId;
    private List<Amount> amounts;
    private InvokeScriptTransactionSender txSender;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private String key1;
    private String key2;
    private String assetIdStr;
    private String intArg;
    private String doubleIntArg;
    private long invokeFee;
    private long assetAmountValue;
    private long wavesAmountValue;
    private long secondWavesAmountValue;

    @Test
    @DisplayName("subscribe invoke double nested: " + callerForScript)
    void subscribeInvokeWithDoubleNestedCaller() {
        prepareDoubleNestedTest(callerForScript);
        txSender.invokeSender();
        String txId = txSender.getInvokeScriptId();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheckDoubleNestedInvoke(txId, getTxIndex(), callerForScript);
    }

    @Test
    @DisplayName("subscribe invoke double nested: " + originCallerForScript)
    void subscribeInvokeWithDoubleNestedOriginCaller() {
        prepareDoubleNestedTest(originCallerForScript);
        txSender.invokeSender();
        String txId = txSender.getInvokeScriptId();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheckDoubleNestedInvoke(txId, getTxIndex(), originCallerForScript);
    }

    public void assertionsCheckDoubleNestedInvoke(String txId, int txIndex, String callerType) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPK, txId, txIndex, dAppPKHash),
                () -> checkMainMetadata(txIndex, dAppAddress, dAppFunctionName),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, otherDAppAddress),
                () -> checkArgumentsMetadata(txIndex, 1, BINARY_BASE58, assetDAppAddress),
                () -> checkArgumentsMetadata(txIndex, 2, INTEGER, intArg),
                () -> checkArgumentsMetadata(txIndex, 3, STRING, key1),
                () -> checkArgumentsMetadata(txIndex, 4, STRING, key2),
                () -> checkArgumentsMetadata(txIndex, 5, BINARY_BASE58, assetIdStr),

                () -> checkDataMetadata(txIndex, 0, INTEGER, key1, doubleIntArg),
                () -> checkDataMetadata(txIndex, 1, INTEGER, key2, String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkResultInvokesMetadata(txIndex, 0, assetDAppAddress, key1),

                () -> checkInvokesMetadataCallArgs(txIndex, 0, 0, INTEGER, intArg),
                () -> checkInvokesMetadataCallArgs(txIndex, 0, 1, BINARY_VALUE, assetIdStr),
                () -> checkInvokesMetadataCallArgs(txIndex, 0, 2, BINARY_VALUE, otherDAppAddress),

                () -> checkResultInvokesMetadataPayments(txIndex, 0, 0, assetIdStr, assetAmountValue)
        );

        if (callerType.equals(callerForScript)) {
            checkCallerForScript(txIndex);
        } else if (callerType.equals(originCallerForScript)) {
            checkOriginCallerForScript(txIndex);
        }
    }

    private void checkCallerForScript(int txIndex) {
        assertAll(
                () -> checkStateChangesTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, dAppAddress),
                () -> checkStateChangesNestedTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, dAppAddress),
                () -> checkResultNestedInvokes(txIndex, 0, 0, otherDAppAddress, testData.getKeyForDAppEqualBaz()),
                () -> checkNestedInvokesMetadataCallArgs(txIndex, 0, 0, 0, INTEGER, intArg),
                () -> checkStateChangesDoubleNestedTransfers(txIndex, 0, 0, 0, WAVES_STRING_ID, secondWavesAmountValue, assetDAppAddress),
                () -> checkDataMetadata(txIndex, 0, INTEGER, key1, calcBalances.getInvokeResultData()),

                () -> checkStateUpdateBalance(txIndex,
                        0,
                        dAppAddress,
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        1,
                        dAppAddress,
                        assetIdStr,
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        2,
                        assetDAppAddress,
                        WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        3,
                        assetDAppAddress,
                        assetIdStr,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        4,
                        otherDAppAddress,
                        WAVES_STRING_ID,
                        calcBalances.getOtherDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getOtherDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        5,
                        callerAddress,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),


                () -> checkStateUpdateDataEntries(txIndex, 0,
                        dAppAddress,
                        key1,
                        calcBalances.getInvokeResultData()),

                () -> checkStateUpdateDataEntries(txIndex, 1,
                        dAppAddress,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
        );
    }

    private void checkOriginCallerForScript(int txIndex) {
        assertAll(
                () -> checkStateChangesTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, callerAddress),
                () -> checkStateChangesNestedTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, callerAddress),

                () -> checkResultNestedInvokes(txIndex, 0, 0, otherDAppAddress, testData.getKeyForDAppEqualBaz()),

                () -> checkNestedInvokesMetadataCallArgs(txIndex, 0, 0, 0, INTEGER, intArg),

                () -> checkStateChangesDoubleNestedTransfers(txIndex, 0, 0, 0, WAVES_STRING_ID, secondWavesAmountValue, callerAddress),

                () -> checkDataMetadata(txIndex, 0,
                        INTEGER,
                        key1,
                        calcBalances.getInvokeResultData()),

                () -> checkStateUpdateBalance(txIndex,
                        0,
                        dAppAddress,
                        assetIdStr,
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        1,
                        assetDAppAddress,
                        WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        2,
                        assetDAppAddress,
                        assetIdStr,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        3,
                        callerAddress,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        4,
                        otherDAppAddress,
                        WAVES_STRING_ID,
                        calcBalances.getOtherDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getOtherDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddress, key1, calcBalances.getInvokeResultData()),

                () -> checkStateUpdateDataEntries(txIndex, 1, dAppAddress, key2, String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
        );
    }

    private void prepareDoubleNestedTest(String callerType) {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForDoubleNestedTest(SUM_FEE, callerType, callerType);
        async(
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = testData.getCallerAddress();
                    callerPK = testData.getCallerPublicKey();
                },
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppPKHash = Base58.encode(dAppAccount.address().publicKeyHash());
                    dAppAddress = testData.getDAppAddress();
                },
                () -> {
                    otherDAppAccount = testData.getOtherDAppAccount();
                    otherDAppAddress = testData.getOtherDAppAddress();
                },
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = testData.getAssetDAppAddress();
                },
                () -> {
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                },
                () -> fromHeight = node().getHeight(),
                () -> amounts = testData.getOtherAmounts(),
                () -> key1 = testData.getKeyForDAppEqualBar(),
                () -> key2 = testData.getKey2ForDAppEqualBalance(),
                () -> invokeFee = testData.getInvokeFee(),
                () -> setVersion(LATEST_VERSION),
                () -> assetAmountValue = testData.getAssetAmount().value(),
                () -> wavesAmountValue = testData.getWavesAmount().value(),
                () -> secondWavesAmountValue = testData.getSecondWavesAmount().value(),
                () -> dAppFunctionName = testData.getDAppCall().getFunction().name(),
                () -> {
                    intArg = String.valueOf(testData.getIntArg());
                    doubleIntArg = String.valueOf(testData.getIntArg() * 2);
                }
        );
        DAppCall dAppCall = testData.getDAppCall();
        txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calculateBalancesForTest(callerType);
    }

    private void calculateBalancesForTest(String callerType) {
        if (callerType.equals(callerForScript)) {
            calcBalances.balancesAfterDoubleNestedForCaller(
                    caller.address(),
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    amounts,
                    assetId
            );
        } else if (callerType.equals(originCallerForScript)) {
            calcBalances.balancesAfterDoubleNestedForOriginCaller(
                    caller.address(),
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    amounts,
                    assetId
            );
        }
    }
}
