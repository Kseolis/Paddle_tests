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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeIssueGrpcTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Issue")
    void prepareDataForIssueTests() {
        testData.prepareDataForIssueTests();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        final AssetId assetId = testData.getAssetId();
        final DAppCall dAppCall = testData.getDAppCall();
        final Account caller = testData.getCallerAccount();
        final Account assetDAppAccount = testData.getAssetDAppAccount();
        final List<Amount> amounts = testData.getAmounts();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);

        setVersion(LATEST_VERSION);
        calcBalances.balancesAfterReissueAssetInvoke(caller, assetDAppAccount, amounts, assetId);
        txSender.invokeSender();

        final String txId = txSender.getInvokeScriptId();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);

        assertionsCheck(
                Long.parseLong(getIssueAssetData().get(VOLUME)),
                Long.parseLong(testData.getAssetDataForIssue().get(VOLUME)),
                txId
        );
    }

    private void assertionsCheck(long issueAssetDataVolume, long assetDataForIssueVolume, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkIssueAssetMetadata(0, 1, testData.getAssetDataForIssue()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getAssetDAppAddress(),
                        null,
                        0, issueAssetDataVolume),
                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getAssetDAppAddress(),
                        null,
                        0, assetDataForIssueVolume),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), issueAssetDataVolume),
                () -> checkStateUpdateAssets(0, 1, testData.getAssetDataForIssue(), assetDataForIssueVolume)
        );
    }
}
