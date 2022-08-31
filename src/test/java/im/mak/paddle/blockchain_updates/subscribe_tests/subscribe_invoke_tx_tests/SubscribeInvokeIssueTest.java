package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeIssueTest extends InvokeBaseTest {
    private PrepareInvokeTestsData testsData;

    @BeforeEach
    void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Issue")
    void prepareDataForIssueTests() {
        testsData.prepareDataForIssueTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (getCallerAccount(), getAssetDAppAccount(), getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterReissueAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());

        txSender.invokeSender();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getAssetDAppAccount());

        assertionsCheck(
                Long.parseLong(getIssueAssetData().get(VOLUME)),
                Long.parseLong(getAssetDataForIssue().get(VOLUME))
        );
    }

    private void assertionsCheck(long issueAssetDataVolume, long assetDataForIssueVolume) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(getInvokeFee(), getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkIssueAssetMetadata(0, 1, getAssetDataForIssue()),

                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getAssetDAppAddress(),
                        null,
                        0, issueAssetDataVolume),
                () -> checkStateUpdateBalance(0,
                        2,
                        getAssetDAppAddress(),
                        null,
                        0, assetDataForIssueVolume),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), issueAssetDataVolume),
                () -> checkStateUpdateAssets(0, 1, getAssetDataForIssue(), assetDataForIssueVolume)
        );
    }
}
