package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
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
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.BINARY_BASE58;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeBurnGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private String assetIdStr;
    private DAppCall dAppCall;
    private String dAppFunctionName;
    private Account caller;
    private String callerAddress;
    private Account assetDAppAccount;
    private String assetDAppAddress;
    private String assetDAppPKHash;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForBurnTests();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        async(
                () -> {
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                },
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppFunctionName = dAppCall.getFunction().name();
                },
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = testData.getAssetDAppAddress();
                    assetDAppPKHash = Base58.encode(assetDAppAccount.address().publicKeyHash());
                },
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = testData.getCallerAddress();
                }
        );
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
        assertionsCheck(txId, getTxIndex());
    }

    private void assertionsCheck(String txId, int txIndex) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId, 0, assetDAppPKHash),
                () -> checkMainMetadata(txIndex, assetDAppAddress, dAppFunctionName),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkIssueAssetMetadata(txIndex, 0, getIssueAssetData()),
                () -> checkBurnMetadata(txIndex, 0, assetIdStr, testData.getAssetAmount().value()),
                () -> checkBurnMetadata(txIndex, 1, null, testData.getAssetAmount().value()),

                () -> checkStateUpdateBalance(txIndex,
                        0,
                        callerAddress,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 1, assetDAppAddress, null, 0, testData.getAmountAfterInvokeIssuedAsset()),

                () -> checkStateUpdateBalance(txIndex,
                        2,
                        assetDAppAddress,
                        assetIdStr,
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateAssets(txIndex, 0, getIssueAssetData(), testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(txIndex, 1, testData.getAssetData(), testData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}
