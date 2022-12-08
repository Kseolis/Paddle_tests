package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDoubleNestedCallerTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private static final String callerForScript = "i.caller";
    private static final String originCallerForScript = "i.originCaller";

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("2 tests subscribe invoke double nested: " + callerForScript + " / " + originCallerForScript)
    void subscribeInvokeWithDoubleNested() {
        final String[] callersArray = {callerForScript, originCallerForScript};
        for (String s : callersArray) {
            fromHeight = node().getHeight();
            testData.prepareDataForDoubleNestedTest(SUM_FEE, s, s);
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

            final Account caller = testData.getCallerAccount();
            final Account dAppAccount = testData.getDAppAccount();
            final Account otherDAppAccount = testData.getOtherDAppAccount();
            final Account assetDAppAccount = testData.getAssetDAppAccount();
            final AssetId assetId = testData.getAssetId();
            final DAppCall dAppCall = testData.getDAppCall();
            final List<Amount> amounts = testData.getPayments();

            final InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);

            setVersion(LATEST_VERSION);

            if (s.equals(callerForScript)) {
                calcBalances.balancesAfterDoubleNestedForCaller(
                        caller, dAppAccount, otherDAppAccount, assetDAppAccount, amounts, assetId
                );
            } else if (s.equals(originCallerForScript)) {
                calcBalances.balancesAfterDoubleNestedForOriginCaller(
                        caller, dAppAccount, otherDAppAccount, assetDAppAccount, amounts, assetId
                );
            }
            txSender.invokeSender();

            final String txId = txSender.getInvokeScriptId();

            toHeight = node().getHeight();
            subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
            prepareInvoke(dAppAccount, testData);
            assertionsCheckDoubleNestedInvoke(testData, calcBalances, txId, 0, s);
        }
    }

    public static void assertionsCheckDoubleNestedInvoke
            (PrepareInvokeTestsData data, InvokeCalculationsBalancesAfterTx calcBalances, String txId, int txIndex, String callerType) {
        String key1 = data.getKeyForDAppEqualBar();
        String key2 = data.getKey2ForDAppEqualBalance();
        String assetId = data.getAssetId().toString();
        assertAll(
                () -> checkInvokeSubscribeTransaction(data.getInvokeFee(), data.getCallerPublicKey(), txId, txIndex),
                () -> checkMainMetadata(txIndex),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, data.getOtherDAppAddress()),
                () -> checkArgumentsMetadata(txIndex, 1, BINARY_BASE58, data.getAssetDAppAddress()),
                () -> checkArgumentsMetadata(txIndex, 2, INTEGER, String.valueOf(data.getIntArg())),
                () -> checkArgumentsMetadata(txIndex, 3, STRING, key1),
                () -> checkArgumentsMetadata(txIndex, 4, STRING, key2),
                () -> checkArgumentsMetadata(txIndex, 5, BINARY_BASE58, assetId),

                () -> checkDataMetadata(txIndex, 0,
                        INTEGER,
                        key1,
                        String.valueOf(data.getIntArg() * 2)),

                () -> checkDataMetadata(txIndex, 1,
                        INTEGER,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkResultInvokesMetadata(txIndex, 0, data.getAssetDAppAddress(), key1),

                () -> checkInvokesMetadataCallArgs(txIndex, 0, 0, INTEGER, String.valueOf(data.getIntArg())),
                () -> checkInvokesMetadataCallArgs(txIndex, 0, 1, BINARY_VALUE, assetId),
                () -> checkInvokesMetadataCallArgs(txIndex, 0, 2, BINARY_VALUE, data.getOtherDAppAddress()),

                () -> checkResultInvokesMetadataPayments(txIndex, 0, 0, assetId, data.getAssetAmount().value())
        );

        if (callerType.equals(callerForScript)) {
            assertAll(
                    () -> checkStateChangesTransfers(txIndex, 0, 0,
                            WAVES_STRING_ID,
                            data.getWavesAmount().value(),
                            data.getDAppAddress()
                    ),

                    () -> checkStateChangesNestedTransfers(txIndex, 0, 0,
                            WAVES_STRING_ID,
                            data.getWavesAmount().value(),
                            data.getDAppAddress()
                    ),

                    () -> checkResultNestedInvokes(txIndex, 0, 0,
                            data.getOtherDAppAddress(),
                            data.getKeyForDAppEqualBaz()
                    ),

                    () -> checkNestedInvokesMetadataCallArgs(txIndex, 0, 0, 0,
                            INTEGER,
                            String.valueOf(data.getIntArg())
                    ),

                    () -> checkStateChangesDoubleNestedTransfers(txIndex, 0, 0, 0,
                            WAVES_STRING_ID,
                            data.getSecondWavesAmount().value(),
                            data.getAssetDAppAddress()
                    ),

                    () -> checkDataMetadata(txIndex, 0,
                            INTEGER,
                            key1,
                            calcBalances.getInvokeResultData()),

                    () -> checkStateUpdateBalance(txIndex,
                            0,
                            data.getDAppAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getDAppBalanceWavesBeforeTransaction(),
                            calcBalances.getDAppBalanceWavesAfterTransaction()),
                    () -> checkStateUpdateBalance(txIndex,
                            1,
                            data.getDAppAddress(),
                            assetId,
                            calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                            calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            2,
                            data.getAssetDAppAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getAccBalanceWavesBeforeTransaction(),
                            calcBalances.getAccBalanceWavesAfterTransaction()),
                    () -> checkStateUpdateBalance(txIndex,
                            3,
                            data.getAssetDAppAddress(),
                            assetId,
                            calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                            calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            4,
                            data.getOtherDAppAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getOtherDAppBalanceWavesBeforeTransaction(),
                            calcBalances.getOtherDAppBalanceWavesAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            5,
                            data.getCallerAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getCallerBalanceWavesBeforeTransaction(),
                            calcBalances.getCallerBalanceWavesAfterTransaction()),


                    () -> checkStateUpdateDataEntries(txIndex, 0,
                            data.getDAppAddress(),
                            key1,
                            calcBalances.getInvokeResultData()),

                    () -> checkStateUpdateDataEntries(txIndex, 1,
                            data.getDAppAddress(),
                            key2,
                            String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
            );
        } else if (callerType.equals(originCallerForScript)) {
            assertAll(
                    () -> checkStateChangesTransfers(txIndex, 0, 0,
                            WAVES_STRING_ID,
                            data.getWavesAmount().value(),
                            data.getCallerAddress()
                    ),

                    () -> checkStateChangesNestedTransfers(txIndex, 0, 0,
                            WAVES_STRING_ID,
                            data.getWavesAmount().value(),
                            data.getCallerAddress()
                    ),

                    () -> checkResultNestedInvokes(txIndex, 0, 0,
                            data.getOtherDAppAddress(),
                            data.getKeyForDAppEqualBaz()
                    ),

                    () -> checkNestedInvokesMetadataCallArgs(txIndex, 0, 0, 0,
                            INTEGER,
                            String.valueOf(data.getIntArg())
                    ),

                    () -> checkStateChangesDoubleNestedTransfers(txIndex, 0, 0, 0,
                            WAVES_STRING_ID,
                            data.getSecondWavesAmount().value(),
                            data.getCallerAddress()
                    ),

                    () -> checkDataMetadata(txIndex, 0,
                            INTEGER,
                            key1,
                            calcBalances.getInvokeResultData()),

                    () -> checkStateUpdateBalance(txIndex,
                            0,
                            data.getDAppAddress(),
                            assetId,
                            calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                            calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            1,
                            data.getAssetDAppAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getAccBalanceWavesBeforeTransaction(),
                            calcBalances.getAccBalanceWavesAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            2,
                            data.getAssetDAppAddress(),
                            assetId,
                            calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                            calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            3,
                            data.getCallerAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getCallerBalanceWavesBeforeTransaction(),
                            calcBalances.getCallerBalanceWavesAfterTransaction()),

                    () -> checkStateUpdateBalance(txIndex,
                            4,
                            data.getOtherDAppAddress(),
                            WAVES_STRING_ID,
                            calcBalances.getOtherDAppBalanceWavesBeforeTransaction(),
                            calcBalances.getOtherDAppBalanceWavesAfterTransaction()),

                    () -> checkStateUpdateDataEntries(txIndex, 0,
                            data.getDAppAddress(),
                            key1,
                            calcBalances.getInvokeResultData()),

                    () -> checkStateUpdateDataEntries(txIndex, 1,
                            data.getDAppAddress(),
                            key2,
                            String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
            );
        }
    }
}
