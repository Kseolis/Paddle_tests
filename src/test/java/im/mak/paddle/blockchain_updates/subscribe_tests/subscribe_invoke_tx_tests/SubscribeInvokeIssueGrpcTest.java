package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.account.Address;
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
import java.util.Map;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeIssueGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private DAppCall dAppCall;
    private String dAppFunctionName;
    private Account caller;
    private String callerPK;
    private Address callerAddress;
    private String callerAddressString;
    private long callerWavesBalanceBeforeTx;
    private long callerWavesBalanceAfterTx;
    private Account assetDAppAccount;
    private String assetDAppAddress;
    private String assetDAppPKHash;
    private Map<String, String> issueAssetData;
    private long issueAssetDataVolume;
    private Map<String, String> assetDataForIssue;
    private long assetDataForIssueVolume;
    private long invokeFee;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForIssueTests();
        async(
                () -> {
                    caller = testData.getCallerAccount();
                    callerPK = testData.getCallerPublicKey();
                    callerAddress = caller.address();
                    callerAddressString = testData.getCallerAddress();
                },
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = testData.getAssetDAppAddress();
                    assetDAppPKHash = Base58.encode(assetDAppAccount.address().publicKeyHash());
                },
                () -> {
                    issueAssetData = getIssueAssetData();
                    issueAssetDataVolume = Long.parseLong(issueAssetData.get(VOLUME));
                },
                () -> {
                    assetDataForIssue = testData.getAssetDataForIssue();
                    assetDataForIssueVolume = Long.parseLong(assetDataForIssue.get(VOLUME));
                },

                () -> invokeFee = testData.getInvokeFee(),
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppFunctionName = dAppCall.getFunction().name();
                }
        );
        AssetId assetId = testData.getAssetId();
        List<Amount> amounts = testData.getPayments();
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterReissueAssetInvoke(callerAddress, assetDAppAccount.address(), amounts, assetId);
        callerWavesBalanceBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction();
        callerWavesBalanceAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction();
    }

    @Test
    @DisplayName("subscribe invoke with Issue")
    void prepareDataForIssueTests() {
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
        txSender.invokeSender(LATEST_VERSION);
        String txId = txSender.getInvokeScriptId();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        assertionsCheck(txId, getTxIndex());
    }

    private void assertionsCheck(String txId, int txIndex) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPK, txId, txIndex, assetDAppPKHash),
                () -> checkMainMetadata(txIndex, assetDAppAddress, dAppFunctionName),
                () -> checkIssueAssetMetadata(txIndex, 0, issueAssetData),
                () -> checkIssueAssetMetadata(txIndex, 1, assetDataForIssue),
                () -> checkStateUpdateBalance(txIndex, 0, callerAddressString, WAVES_STRING_ID, callerWavesBalanceBeforeTx, callerWavesBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, assetDAppAddress, null, 0, issueAssetDataVolume),
                () -> checkStateUpdateBalance(txIndex, 2, assetDAppAddress, null, 0, assetDataForIssueVolume),
                () -> checkStateUpdateAssets(txIndex, 0, issueAssetData, issueAssetDataVolume),
                () -> checkStateUpdateAssets(txIndex, 1, assetDataForIssue, assetDataForIssueVolume)
        );
    }
}
