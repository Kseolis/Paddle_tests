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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeReissueTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testData;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Reissue")
    void subscribeInvokeWithReissue() {
        testData.prepareDataForReissueTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testData.getCallerAccount(), testData.getAssetDAppAccount(), testData.getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterReissueAssetInvoke(testData.getCallerAccount(), testData.getAssetDAppAccount(), testData.getAmounts(), testData.getAssetId());

        txSender.invokeSender();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, testData.getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testData.getAssetDAppAccount(), testData);

        checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey());
        assertionsCheck();
    }

    private void assertionsCheck() {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkReissueMetadata(0, 0,
                        testData.getAssetId().toString(),
                        testData.getAssetAmount().value(),
                        true),

                () -> checkReissueMetadata(0, 1,
                        null,
                        testData.getAssetAmount().value(),
                        true),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getAssetDAppAddress(),
                        null,
                        0, testData.getAmountAfterInvokeIssuedAsset()),

                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getAssetDAppAddress(),
                        testData.getAssetId().toString(),
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(0, 1, testData.getAssetData(), testData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}
*/
