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
    private static PrepareInvokeTestsData testsData;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Reissue")
    void subscribeInvokeWithReissue() {
        testsData.prepareDataForReissueTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testsData.getCallerAccount(), testsData.getAssetDAppAccount(), testsData.getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterReissueAssetInvoke(testsData.getCallerAccount(), testsData.getAssetDAppAccount(), testsData.getAmounts(), testsData.getAssetId());

        txSender.invokeSender();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, testsData.getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testsData.getAssetDAppAccount(), testsData);

        checkInvokeSubscribeTransaction(testsData.getInvokeFee(), testsData.getCallerPublicKey());
        assertionsCheck();
    }

    private void assertionsCheck() {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testsData.getInvokeFee(), testsData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkReissueMetadata(0, 0,
                        testsData.getAssetId().toString(),
                        testsData.getAssetAmount().value(),
                        true),

                () -> checkReissueMetadata(0, 1,
                        null,
                        testsData.getAssetAmount().value(),
                        true),

                () -> checkStateUpdateBalance(0,
                        0,
                        testsData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testsData.getAssetDAppAddress(),
                        null,
                        0, testsData.getAmountAfterInvokeIssuedAsset()),

                () -> checkStateUpdateBalance(0,
                        2,
                        testsData.getAssetDAppAddress(),
                        testsData.getAssetId().toString(),
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), testsData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(0, 1, testsData.getAssetData(), testsData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}
*/
