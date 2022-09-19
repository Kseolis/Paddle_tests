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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeReissueGrpcTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Reissue")
    void subscribeInvokeWithReissue() {
        testData.prepareDataForReissueTests();
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

        String txId = txSender.getInvokeScriptId();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);

        assertionsCheck(txId);
    }

    private void assertionsCheck(String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId),
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
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getAssetDAppAddress(),
                        null,
                        0, testData.getAmountAfterInvokeIssuedAsset()),

                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getAssetDAppAddress(),
                        testData.getAssetId().toString(),
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(0, 1, testData.getAssetData(), testData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}
