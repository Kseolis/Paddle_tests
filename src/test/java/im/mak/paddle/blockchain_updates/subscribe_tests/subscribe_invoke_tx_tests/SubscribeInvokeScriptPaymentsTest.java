/*
package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeScriptPaymentsTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testData;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with payments")
    void subscribeInvokeWithScriptPayments() {
        testData.prepareDataForPaymentsTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testData.getCallerAccount(), testData.getDAppAccount(), testData.getDAppCall(), testData.getAmounts());

        setVersion(LATEST_VERSION);
        balancesAfterCallerInvokeAsset(testData.getCallerAccount(), testData.getDAppAccount(), testData.getAmounts(), testData.getAssetId());
        txSender.invokeSenderWithPayment();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testData.getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testData.getDAppAccount(), testData);

        assertionsCheck(testData.getWavesAmount().value(),
                testData.getAssetAmount().value(),
                testData.getAssetId().toString(),
                String.valueOf(testData.getIntArg()));
    }

    private void assertionsCheck(long paymentWaves, long paymentAsset, String assetId, String intArg) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey()),
                () -> checkPaymentsSubscribe(0, 0, paymentWaves, ""),
                () -> checkPaymentsSubscribe(0, 1, paymentAsset, assetId),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, INTEGER, intArg),
                () -> checkPaymentMetadata(0, 0, null, paymentWaves),
                () -> checkPaymentMetadata(0, 1, assetId, paymentAsset),
                () -> checkDataMetadata(0, 0, INTEGER, DATA_ENTRY_INT, intArg),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getCallerAddress(),
                        assetId,
                        getCallerBalanceIssuedAssetsBeforeTransaction(), getCallerBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getDAppAddress(),
                        WAVES_STRING_ID,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        3,
                        testData.getDAppAddress(),
                        assetId,
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        4,
                        testData.getDAppAddress(),
                        null,
                        0, getIssueAssetVolume()),
                () -> checkStateUpdateDataEntries(0, 0, getDAppAccountAddress(), DATA_ENTRY_INT, intArg),
                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume())
        );
    }
}
*/
