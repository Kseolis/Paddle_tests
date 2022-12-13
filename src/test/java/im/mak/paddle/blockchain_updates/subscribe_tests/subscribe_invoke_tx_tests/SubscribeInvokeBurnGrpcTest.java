package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.BINARY_BASE58;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeBurnGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private DAppCall dAppCall;
    private Account caller;
    private Account assetDAppAccount;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForBurnTests();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        assetId = testData.getAssetId();
        dAppCall = testData.getDAppCall();
        caller = testData.getCallerAccount();
        assetDAppAccount = testData.getAssetDAppAccount();
        List<Amount> amounts = testData.getOtherAmounts();
        calcBalances.balancesAfterBurnAssetInvoke(caller.address(), assetDAppAccount.address(), amounts, assetId);
        setVersion(LATEST_VERSION);
    }

    @Test
    @DisplayName("subscribe invoke with Burn")
    void subscribeInvokeWithBurn() {
        final InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
        txSender.invokeSender();
        final String txId = txSender.getInvokeScriptId();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);
        assertionsCheck(txId);
    }

    private void assertionsCheck(String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId, 0),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, assetId.toString()),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkBurnMetadata(0, 0, assetId.toString(), testData.getAssetAmount().value()),
                () -> checkBurnMetadata(0, 1, null, testData.getAssetAmount().value()),

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
                        assetId.toString(),
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(0, 1, testData.getAssetData(), testData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}
