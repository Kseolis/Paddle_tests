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

public class SubscribeInvokeDoubleNestingTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke double nesting")
    void subscribeInvokeWithDoubleNesting() {
        fromHeight = node().getHeight();
        long fee = SUM_FEE + (ONE_WAVES * 2);
        testData.prepareDataForDoubleNestingTest(SUM_FEE);
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        final AssetId assetId = testData.getAssetId();
        final DAppCall dAppCall = testData.getDAppCall();
        final Account caller = testData.getCallerAccount();
        final Account dAppAccount = testData.getDAppAccount();
        final Account assetDAppAccount = testData.getAssetDAppAccount();
        final List<Amount> amounts = testData.getAmounts();

        final InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
        setVersion(LATEST_VERSION);
        calcBalances.balancesAfterDAppToDApp(caller, dAppAccount, assetDAppAccount, amounts, assetId);
        txSender.invokeSender();

        final String txId = txSender.getInvokeScriptId();

        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        prepareInvoke(dAppAccount, testData);
        assertionsCheckDoubleNestingInvoke(testData, calcBalances, txId, 0);
    }

    public static void assertionsCheckDoubleNestingInvoke
            (PrepareInvokeTestsData data, InvokeCalculationsBalancesAfterTx calcBalances, String txId, int txIndex) {
        String key1 = data.getKey1ForDAppEqualBar();
        String key2 = data.getKey2ForDAppEqualBalance();
        String assetId = data.getAssetId().toString();
        assertAll(
                () -> checkInvokeSubscribeTransaction(data.getInvokeFee(), data.getCallerPublicKey(), txId, txIndex),
                () -> checkMainMetadata(txIndex),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, data.getAssetDAppAddress()),
                () -> checkArgumentsMetadata(txIndex, 1, INTEGER, String.valueOf(data.getIntArg())),
                () -> checkArgumentsMetadata(txIndex, 2, STRING, key1),
                () -> checkArgumentsMetadata(txIndex, 3, STRING, key2),
                () -> checkArgumentsMetadata(txIndex, 4, BINARY_BASE58, assetId),

                () -> checkDataMetadata(txIndex, 1,
                        INTEGER,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkResultInvokesMetadata(txIndex, 0,
                        data.getAssetDAppAddress(),
                        key1),
                () -> checkResultInvokesMetadataPayments(txIndex, 0, 0, assetId, data.getAssetAmount().value()),

                () -> checkStateChangesTransfers(txIndex, 0, 0,
                        WAVES_STRING_ID,
                        data.getWavesAmount().value(),
                        data.getDAppAddress()
                ),
                () -> checkStateChangesBurn(txIndex, 0, 0, data.getAssetAmount()),
                () -> checkStateChangesReissue(txIndex, 0, 0, data),
                () -> checkStateChangesData(txIndex, 0, 0, data),
                () -> checkStateChangesSponsorFee(txIndex, 0, 0, data),
                () -> checkStateChangesLease(txIndex, 0, 0, data),
                () -> checkStateChangesLeaseCancel(txIndex, 0, 0),

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
    }
}
