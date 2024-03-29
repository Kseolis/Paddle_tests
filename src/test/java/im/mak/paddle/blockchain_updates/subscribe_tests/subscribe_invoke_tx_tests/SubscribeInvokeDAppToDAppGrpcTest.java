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
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDAppToDAppGrpcTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private static AssetId assetId;
    private static Account caller;
    private static Account dAppAccount;
    private static Account assetDAppAccount;
    private static DAppCall dAppCall;
    private static List<Amount> amounts;
    private static InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForDAppToDAppTests(SUM_FEE);
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        async(
                () -> assetId = testData.getAssetId(),
                () -> caller = testData.getCallerAccount(),
                () -> dAppAccount = testData.getDAppAccount(),
                () -> assetDAppAccount = testData.getAssetDAppAccount(),
                () -> amounts = testData.getOtherAmounts(),
                () -> dAppCall = testData.getDAppCall()
        );
    }


    @Test
    @DisplayName("subscribe invoke dApp to dApp")
    void subscribeInvokeWithDAppToDApp() {
        fromHeight = node().getHeight();
        calcBalances.balancesAfterDAppToDApp(caller.address(), dAppAccount.address(), assetDAppAccount.address(), amounts, assetId);

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
        txSender.invokeSender(LATEST_VERSION);
        String txId = txSender.getInvokeScriptId();

        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheckDAppToDAppInvoke(testData, calcBalances, txId, getTxIndex());
    }

    public static void assertionsCheckDAppToDAppInvoke(PrepareInvokeTestsData data, InvokeCalculationsBalancesAfterTx calcBalances, String txId, int txIndex) {
        String key1 = data.getKeyForDAppEqualBar();
        String key2 = data.getKey2ForDAppEqualBalance();
        assertAll(
                () -> checkInvokeSubscribeTransaction(data.getInvokeFee(), data.getCallerPublicKey(), txId, txIndex, data.getDAppPublicKeyHash()),
                () -> checkMainMetadata(txIndex, data.getDAppAddress(), data.getDAppCall().getFunction().name()),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, data.getAssetDAppAddress()),
                () -> checkArgumentsMetadata(txIndex, 1, INTEGER, String.valueOf(data.getIntArg())),
                () -> checkArgumentsMetadata(txIndex, 2, STRING, key1),
                () -> checkArgumentsMetadata(txIndex, 3, STRING, key2),
                () -> checkArgumentsMetadata(txIndex, 4, BINARY_BASE58, data.getAssetId().toString()),

                () -> checkDataMetadata(txIndex, 1,
                        INTEGER,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkResultInvokesMetadata(txIndex, 0,
                        data.getAssetDAppAddress(),
                        key1),
                () -> checkResultInvokesMetadataPayments(txIndex, 0, 0, data.getAssetId().toString(), data.getAssetAmount().value()),

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
                        data.getAssetId().toString(),
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
                        data.getAssetId().toString(),
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
