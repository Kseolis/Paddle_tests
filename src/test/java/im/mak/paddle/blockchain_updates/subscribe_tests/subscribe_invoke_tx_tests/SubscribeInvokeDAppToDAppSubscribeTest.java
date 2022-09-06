package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDAppToDAppSubscribeTest extends InvokeBaseSubscribeTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe dApp to dApp")
    void subscribeInvokeWithDAppToDApp() {
        testData.prepareDataForDAppToDAppTests(SUM_FEE + ONE_WAVES);
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        final AssetId assetId = testData.getAssetId();
        final DAppCall dAppCall = testData.getDAppCall();
        final Account caller = testData.getCallerAccount();
        final Account dAppAccount = testData.getDAppAccount();
        final Account assetDAppAccount = testData.getAssetDAppAccount();
        final List<Amount> amounts = testData.getAmounts();

        final InvokeScriptTransactionSender txSender =
                new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
        setVersion(LATEST_VERSION);
        calcBalances.balancesAfterDAppToDApp(caller, dAppAccount, assetDAppAccount, amounts, assetId);
        txSender.invokeSender();

        final String txId = txSender.getInvokeScriptId();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, dAppAccount, height, height, txId);
        prepareInvoke(dAppAccount, testData);

        assertionsCheck(
                testData.getKey1ForDAppEqualBar(),
                testData.getKey2ForDAppEqualBalance(),
                assetId.toString(),
                txId
        );
    }

    private void assertionsCheck(String key1, String key2, String assetId, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, testData.getAssetDAppAddress()),
                () -> checkArgumentsMetadata(0, 1, INTEGER, String.valueOf(testData.getIntArg())),
                () -> checkArgumentsMetadata(0, 2, STRING, key1),
                () -> checkArgumentsMetadata(0, 3, STRING, key2),
                () -> checkArgumentsMetadata(0, 4, BINARY_BASE58, assetId),

                () -> checkDataMetadata(0, 0,
                        INTEGER,
                        key1,
                        calcBalances.getInvokeResultData()),
                () -> checkDataMetadata(0, 1,
                        INTEGER,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkResultInvokesMetadata(0, 0,
                        testData.getAssetDAppAddress(),
                        key1),
                () -> checkResultInvokesMetadataPayments(0, 0, 0, assetId, testData.getAssetAmount().value()),
                () -> checkResultInvokesMetadataStateChanges(0, 0, 0,
                        WAVES_STRING_ID,
                        testData.getDAppAddress(),
                        testData.getWavesAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getDAppAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getDAppAddress(),
                        assetId,
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getAssetDAppAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        3,
                        testData.getAssetDAppAddress(),
                        assetId,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        4,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(0, 0,
                        testData.getDAppAddress(),
                        key1,
                        calcBalances.getInvokeResultData()),
                () -> checkStateUpdateDataEntries(0, 1,
                        testData.getDAppAddress(),
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
        );
    }
}
