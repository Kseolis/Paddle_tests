package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeSponsorFeeGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private DAppCall dAppCall;
    private Account caller;
    private Address callerAddress;
    private String callerAddressStr;
    private String callerPK;
    private long callerBalanceWavesBeforeTx;
    private long callerBalanceWavesAfterTx;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressStr;
    private AssetId assetId;
    private String assetIdStr;
    private long invokeFee;
    private List<Amount> payments;
    private long payment;
    private Map<String, String> assetData;
    private long assetDataVolume;
    private Map<String, String> issueAssetData;
    private long issueAssetDataVolume;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForSponsorFeeTests();
        setVersion(LATEST_VERSION);
        async(
                () -> {
                    dAppCall = testData.getDAppCall();
                    invokeFee = testData.getInvokeFee();
                },
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = caller.address();
                    callerAddressStr = testData.getCallerAddress();
                    callerPK = testData.getCallerPublicKey();
                },
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                    assetDAppAddressStr = testData.getAssetDAppAddress();
                },
                () -> {
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                },
                () -> payments = testData.getOtherAmounts(),
                () -> payment = testData.getAssetAmount().value(),
                () -> {
                    issueAssetData = getIssueAssetData();
                    issueAssetDataVolume = Long.parseLong(issueAssetData.get(VOLUME));
                },
                () -> {
                    assetData = testData.getAssetData();
                    assetDataVolume = Long.parseLong(assetData.get(VOLUME));
                }

        );
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(callerAddress, assetDAppAddress, payments, assetId);
        async(
                () -> callerBalanceWavesBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction(),
                () -> callerBalanceWavesAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction()
        );
    }

    @Test
    @DisplayName("subscribe invoke with SponsorFee")
    void subscribeInvokeWithSponsorFee() {
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
        txSender.invokeSender();
        String txId = txSender.getInvokeScriptId();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);
        assertionsCheck(txId, getTxIndex());
    }

    private void assertionsCheck(String txId, int txIndex) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPK, txId, txIndex),
                () -> checkMainMetadata(txIndex),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkIssueAssetMetadata(txIndex, 0, issueAssetData),
                () -> checkSponsorFeeMetadata(txIndex, 0, assetIdStr, payment),
                () -> checkSponsorFeeMetadata(txIndex, 1, null, payment),
                () -> checkStateUpdateBalance(txIndex, 0, callerAddressStr, WAVES_STRING_ID, callerBalanceWavesBeforeTx, callerBalanceWavesAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, assetDAppAddressStr, null, 0, issueAssetDataVolume),
                () -> checkStateUpdateAssets(txIndex, 0, issueAssetData, issueAssetDataVolume),
                () -> checkStateUpdateAssets(txIndex, 1, assetData, assetDataVolume),
                () -> checkStateUpdateAssetsSponsorship(txIndex, 0, payment),
                () -> checkStateUpdateAssetsSponsorship(txIndex, 1, payment)
        );
    }
}
