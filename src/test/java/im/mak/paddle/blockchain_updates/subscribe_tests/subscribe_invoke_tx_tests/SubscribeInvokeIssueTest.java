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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeIssueTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testsData;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Issue")
    void prepareDataForIssueTests() {
        testsData.prepareDataForIssueTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testsData.getCallerAccount(), testsData.getAssetDAppAccount(), testsData.getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterReissueAssetInvoke(
                testsData.getCallerAccount(),
                testsData.getAssetDAppAccount(),
                testsData.getAmounts(),
                testsData.getAssetId());

        txSender.invokeSender();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, testsData.getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testsData.getAssetDAppAccount(), testsData);

        assertionsCheck(
                Long.parseLong(getIssueAssetData().get(VOLUME)),
                Long.parseLong(testsData.getAssetDataForIssue().get(VOLUME))
        );
    }

    private void assertionsCheck(long issueAssetDataVolume, long assetDataForIssueVolume) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testsData.getInvokeFee(), testsData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkIssueAssetMetadata(0, 1, testsData.getAssetDataForIssue()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testsData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testsData.getAssetDAppAddress(),
                        null,
                        0, issueAssetDataVolume),
                () -> checkStateUpdateBalance(0,
                        2,
                        testsData.getAssetDAppAddress(),
                        null,
                        0, assetDataForIssueVolume),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), issueAssetDataVolume),
                () -> checkStateUpdateAssets(0, 1, testsData.getAssetDataForIssue(), assetDataForIssueVolume)
        );
    }
}
*/
